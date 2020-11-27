package com.github.mattisonchao.node

import com.github.mattisonchao.entity.EndPoint
import com.github.mattisonchao.rpc.CustomController
import com.github.mattisonchao.storage.LogEntries
import com.github.mattisonchao.utils.CountDownClock

interface Node : LifeCycle, RoleTransform, ParliamentMember, Options {

    fun getMetaData(): MetaData

    fun getEndPoint(): EndPoint

    fun getLogEntries(): LogEntries

    fun getElectionClock(): CountDownClock

    fun getCustomController(): CustomController?

}