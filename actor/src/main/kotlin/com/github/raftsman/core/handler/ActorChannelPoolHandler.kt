package com.github.raftsman.core.handler

import com.github.raftsman.MailBoxImp
import com.github.raftsman.core.codec.MsgDecoder
import com.github.raftsman.core.codec.MsgEncoder
import io.netty.channel.Channel
import io.netty.channel.pool.ChannelPoolHandler

class ActorChannelPoolHandler(private val mailBoxImp: MailBoxImp) : ChannelPoolHandler {
    override fun channelReleased(ch: Channel?) {
    }

    override fun channelAcquired(ch: Channel?) {
    }

    override fun channelCreated(ch: Channel) {
        ch.pipeline()
                .addLast(MsgDecoder())
                .addLast(MsgEncoder())
                .addLast(ActorHandler(mailBoxImp))
    }
}