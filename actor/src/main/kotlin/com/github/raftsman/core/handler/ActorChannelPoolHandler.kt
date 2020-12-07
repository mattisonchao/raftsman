package com.github.raftsman.core.handler

import com.github.raftsman.MailBox
import com.github.raftsman.core.codec.MsgDecoder
import com.github.raftsman.core.codec.MsgEncoder
import io.netty.channel.Channel
import io.netty.channel.pool.ChannelPoolHandler

class ActorChannelPoolHandler(private val mailBox: MailBox) : ChannelPoolHandler {
    override fun channelReleased(ch: Channel?) {
    }

    override fun channelAcquired(ch: Channel?) {
    }

    override fun channelCreated(ch: Channel) {
        ch.pipeline()
                .addLast(MsgDecoder())
                .addLast(MsgEncoder())
                .addLast(ActorHandler(mailBox))
    }
}