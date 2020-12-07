package com.github.raftsman

import com.github.raftsman.lifeCycle.LifeCycle

abstract class AbstractActor : Actor, LifeCycle {

    val mailBox: MailBox = MailBoxImp()

    final override fun startup() =
            mailBox
                    .withActor(this)
                    .startup()

    fun startup(port: Int) =
            mailBox
                    .withActor(this)
                    .withPort(port)
                    .startup()

    final override fun shutdown() =
            mailBox.shutdown()
}