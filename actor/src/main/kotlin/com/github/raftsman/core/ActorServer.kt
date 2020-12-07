package com.github.raftsman.core

import com.github.raftsman.MailBoxImp
import com.github.raftsman.core.handler.ActorInitChannelHandler
import com.github.raftsman.lifeCycle.LifeCycle
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler


class ActorServer(private val mailBoxImp: MailBoxImp) : LifeCycle {
    private val bossGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup()
    override fun startup() {
        try {
            val bootstrap = ServerBootstrap()
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .handler(LoggingHandler(LogLevel.TRACE))
                    .option(ChannelOption.SO_BACKLOG, 1024) // 连接超时
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(ActorInitChannelHandler(mailBoxImp))
            val channel = bootstrap.bind(mailBoxImp.port).sync().channel()
            channel.closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }

    override fun shutdown() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

}