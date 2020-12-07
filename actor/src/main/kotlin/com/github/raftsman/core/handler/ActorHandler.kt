package com.github.raftsman.core.handler

import com.github.raftsman.MailBox
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ActorHandler(private val mailBox: MailBox) : SimpleChannelInboundHandler<Any>() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ActorHandler::class.java)
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any) {
        println("receive")
        mailBox.put(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
        logger.error("error { }", cause.printStackTrace())
    }
}