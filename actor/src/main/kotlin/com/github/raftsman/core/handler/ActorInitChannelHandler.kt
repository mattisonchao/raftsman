package com.github.raftsman.core.handler

import com.github.raftsman.MailBox
import com.github.raftsman.core.codec.MsgDecoder
import com.github.raftsman.core.codec.MsgEncoder
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

class ActorInitChannelHandler(val mailBox: MailBox) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
                .addLast(MsgDecoder())
                .addLast(MsgEncoder())
                .addLast(ActorHandler(mailBox))
    }
}