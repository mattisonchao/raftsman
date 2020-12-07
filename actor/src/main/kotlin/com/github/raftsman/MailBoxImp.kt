package com.github.raftsman

import com.github.raftsman.core.ActorClient
import com.github.raftsman.core.ActorServer
import com.github.raftsman.queue.EventFactory
import com.github.raftsman.queue.EventWrapper
import com.github.raftsman.utils.NetUtils
import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.util.DaemonThreadFactory
import io.netty.channel.Channel


class MailBoxImp : MailBox, EventHandler<EventWrapper> {
    private val disruptor = Disruptor(EventFactory(), 1024, DaemonThreadFactory.INSTANCE)
    private val server = ActorServer(this)
    private val client = ActorClient(this)
    var port: Int = NetUtils.getAvailablePort()
    private lateinit var actor: Actor

    override fun onEvent(event: EventWrapper, sequence: Long, endOfBatch: Boolean) =
            actor.onMessageReceived(event.value)

    override fun startup() {
        disruptor.handleEventsWith(this)
        disruptor.start()
        server.startup()
    }

    override fun shutdown() =
            disruptor.shutdown()

    override fun isEmpty() =
            disruptor.ringBuffer.remainingCapacity() == disruptor.ringBuffer.bufferSize.toLong()

    override fun withActor(actor: Actor): MailBox {
        this.actor = actor
        return this
    }

    override fun withPort(port: Int): MailBox {
        this.port = port
        return this
    }

    override fun send(url: Url, message: Any) {
        val channelPool = client.channelPool[url]
        channelPool.acquire().addListener {
            if (it.isSuccess) {
                val ch = it.now as Channel
                ch.writeAndFlush(message)
                channelPool.release(ch)
            }
        }
    }

    fun put(value: Any) =
            disruptor.ringBuffer.publishEvent { event, sequence -> event.value = value }
}
