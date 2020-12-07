package com.github.raftsman.utils

import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.util.*


object NetUtils {

    private const val MAX_AVAILABLE_PORT = 65535
    private const val MIN_AVAILABLE_PORT = 2000

    private fun isLocalPortUsing(port: Int): Boolean = isPortUsing("127.0.0.1", port)

    private fun isPortUsing(host: String, port: Int): Boolean {
        return try {
            Socket(InetAddress.getByName(host), port)
            true
        } catch (e: IOException) {
            false
        }
    }

    fun getAvailablePort(): Int {
        val port: Int = Random().nextInt(MAX_AVAILABLE_PORT) % (MAX_AVAILABLE_PORT - MIN_AVAILABLE_PORT + 1) + MIN_AVAILABLE_PORT
        val using = isLocalPortUsing(port)
        return if (using) {
            getAvailablePort()
        } else {
            port
        }
    }
}