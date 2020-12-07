package com.github.raftsman

import com.github.raftsman.core.ActorClient
import com.github.raftsman.core.ActorServer
import com.github.raftsman.lifeCycle.LifeCycle
import com.github.raftsman.queue.EventFactory
import com.github.raftsman.queue.EventWrapper
import com.github.raftsman.utils.NetUtils
import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.util.DaemonThreadFactory
import io.netty.channel.Channel


class MailBox : EventHandler<EventWrapper>, LifeCycle {
    private val disruptor = Disruptor(EventFactory(), 1024, DaemonThreadFactory.INSTANCE)
    private val server = ActorServer(this)
    private val client = ActorClient(this)
    var port: Int = NetUtils.getAvailablePort()
    lateinit var actor: Actor

    override fun onEvent(event: EventWrapper, sequence: Long, endOfBatch: Boolean) {
        actor.onMessageReceived(event.value)
    }

    override fun startup() {
        disruptor.handleEventsWith(this)
        disruptor.start()
        println(port)
        server.startup()
    }

    override fun shutdown() {
        disruptor.shutdown()
    }

    fun put(value: Any) {
        val ringBuffer = disruptor.ringBuffer
        ringBuffer.publishEvent { event: EventWrapper, sequence: Long ->
            event.value = value
        }
    }

    fun isEmpty() = disruptor.ringBuffer.remainingCapacity() == disruptor.ringBuffer.bufferSize.toLong()

    fun send(url: Url, message: Any) {
        val pool = client.channelPool[url]
        val channelFuture = pool.acquire()
        channelFuture.addListener {
            if (it.isSuccess) {
                val ch = it.now as Channel
                ch.writeAndFlush(message)
                pool.release(ch)
            }
        }
    }


}
