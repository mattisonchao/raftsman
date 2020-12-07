package com.github.raftsman.core.codec

import com.github.raftsman.utils.ProtostuffUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder


class MsgEncoder : MessageToByteEncoder<Any>() {
    override fun encode(ctx: ChannelHandlerContext?, msg: Any, out: ByteBuf) {
        val serializedMsg = ProtostuffUtils.serialize(msg)
        out.writeBytes(serializedMsg)
    }
}