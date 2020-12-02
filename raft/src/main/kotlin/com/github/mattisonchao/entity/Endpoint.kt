package com.github.mattisonchao.entity

data class EndPoint(val host: String, val port: Int)

val EndPoint.toAddress get() = this.host + ":" + port

val String.toEndPoint: EndPoint
    get() = run {
        val splitAddress = this.split(":")
        return EndPoint(splitAddress[0], splitAddress[1].toInt())
    }
