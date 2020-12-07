package com.github.raftsman.core

import com.github.raftsman.MailBox
import com.github.raftsman.Url
import com.github.raftsman.core.handler.ActorChannelPoolHandler
import com.github.raftsman.core.handler.ActorInitChannelHandler
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.pool.AbstractChannelPoolMap
import io.netty.channel.pool.ChannelPoolMap
import io.netty.channel.pool.FixedChannelPool
import io.netty.channel.pool.SimpleChannelPool
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.net.InetSocketAddress

class ActorClient(private val mailBox: MailBox) {
    private val group = NioEventLoopGroup(10)
    private val bootstrap: Bootstrap = Bootstrap()
    var channelPool: ChannelPoolMap<Url, SimpleChannelPool>
    init {
        bootstrap
                .group(group)
                .channel(NioSocketChannel::class.java)
                .handler(LoggingHandler(LogLevel.TRACE))
                .option(ChannelOption.SO_BACKLOG, 1024) // 连接超时
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .handler(ActorInitChannelHandler(mailBox))
        channelPool = object : AbstractChannelPoolMap<Url, SimpleChannelPool>() {
            override fun newPool(key: Url): SimpleChannelPool {
                val actorChannelPoolHandler = ActorChannelPoolHandler(mailBox)
                return FixedChannelPool(bootstrap.remoteAddress(InetSocketAddress.createUnresolved(key.host, key.port)), actorChannelPoolHandler, 4)
            }
        }
    }
}