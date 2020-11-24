package com.github.mattisonchao.node

import com.github.mattisonchao.entity.EndPoint
import com.github.mattisonchao.rpc.CustomController
import com.github.mattisonchao.storage.RocksdbLogEntries
import com.github.mattisonchao.utils.CountDownClock

interface Node : LifeCycle, RoleTransform, ParliamentMember {

    fun getMetaData(): MetaData

    fun getEndPoint(): EndPoint

    fun getLogEntries(): RocksdbLogEntries

    fun getElectionClock(): CountDownClock

    fun getCustomController(): CustomController?

}