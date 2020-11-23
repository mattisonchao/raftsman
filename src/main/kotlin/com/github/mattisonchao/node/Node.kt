package com.github.mattisonchao.node

import com.github.mattisonchao.entity.EndPoint
import com.github.mattisonchao.utils.CountDownClock

interface Node : LifeCycle, RoleTransform {

    fun getMetaData(): MetaData

    fun getEndPoint(): EndPoint

    fun getElectionClock(): CountDownClock
}