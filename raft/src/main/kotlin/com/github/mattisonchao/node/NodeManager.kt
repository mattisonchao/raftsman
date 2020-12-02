package com.github.mattisonchao.node

import com.alipay.remoting.util.ConcurrentHashSet
import com.github.mattisonchao.entity.EndPoint

class NodeManager private constructor() {

    @Volatile
    var leader: EndPoint? = null

    @Volatile
    var peers = ConcurrentHashSet<EndPoint>()

    companion object {
        private val INSTANCE = NodeManager()
        fun getInstance(): NodeManager = INSTANCE
    }

    fun addAddress(endPoint: EndPoint) = peers.add(endPoint)

    fun removeAddress(endPoint: EndPoint) = peers.remove(endPoint)

    fun exists(endPoint: EndPoint) = peers.contains(endPoint)


    fun getAllPeers() = peers
}