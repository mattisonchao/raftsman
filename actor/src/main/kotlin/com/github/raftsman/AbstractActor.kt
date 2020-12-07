package com.github.raftsman

import com.github.raftsman.lifeCycle.LifeCycle

abstract class AbstractActor : Actor, LifeCycle {
    val mailBox: MailBox = MailBox()

    final override fun startup() {
        mailBox.actor = this
        mailBox.startup()
    }

    fun startup(port: Int) {
        mailBox.actor = this
        mailBox.port = port
        mailBox.startup()
    }

    final override fun shutdown() {
        mailBox.shutdown()
    }
}