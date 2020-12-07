package com.gitub.raftsman

import com.github.raftsman.AbstractActor
import com.github.raftsman.Url
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Server2 : AbstractActor() {
    override fun onMessageReceived(message: Any) {
        println("server2 receive $message")
    }
}

fun main() {
    val server2 = Server2()
    println(1)
    GlobalScope.launch {
        while (true) {
            server2.mailBox.send(Url("127.0.0.1", 7999), "come on!")
            delay(1)
        }
    }
    server2.startup(7998)
}