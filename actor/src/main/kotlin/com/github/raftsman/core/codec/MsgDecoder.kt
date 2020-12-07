package com.github.raftsman.core.codec

import com.github.raftsman.utils.ProtostuffUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class MsgDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        val msg = ByteArray(input.readableBytes())
        input.readBytes(msg)
        out.add(ProtostuffUtils.deserialize(msg, String::class.java))
    }
}