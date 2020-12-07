package com.gitub.raftsman

import com.github.raftsman.AbstractActor
import com.github.raftsman.Url

class Server1 : AbstractActor() {
    override fun onMessageReceived(message: Any) {
        println("server1 receive $message")
        mailBox.send(Url("127.0.0.1", 7998), "Hi, 我测试一下")
    }
}

fun main() {
    val server1 = Server1()
    server1.startup(7999)
}