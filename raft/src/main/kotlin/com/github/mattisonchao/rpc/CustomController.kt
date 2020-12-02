package com.github.mattisonchao.rpc

import com.github.mattisonchao.node.Node

interface CustomController {

    fun handleRequest(nodeContext: Node, request: Any): Response<*>

}
