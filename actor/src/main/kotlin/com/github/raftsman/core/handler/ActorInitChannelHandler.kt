package com.github.raftsman.core.handler

import com.github.raftsman.MailBoxImp
import com.github.raftsman.core.codec.MsgDecoder
import com.github.raftsman.core.codec.MsgEncoder
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

class ActorInitChannelHandler(private val mailBoxImp: MailBoxImp) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline()
                .addLast(MsgDecoder())
                .addLast(MsgEncoder())
                .addLast(ActorHandler(mailBoxImp))
    }
}