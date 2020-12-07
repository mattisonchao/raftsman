package com.github.raftsman

import com.github.raftsman.lifeCycle.LifeCycle

interface MailBox : LifeCycle {

    fun send(url: Url, message: Any)

    fun withActor(actor: Actor): MailBox

    fun withPort(port: Int): MailBox

    fun isEmpty(): Boolean

}