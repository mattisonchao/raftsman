package com.github.raftsman

interface Actor {

    fun onMessageReceived(message: Any)

}