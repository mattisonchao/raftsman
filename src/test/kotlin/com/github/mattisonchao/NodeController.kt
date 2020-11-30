package com.github.mattisonchao

import com.github.mattisonchao.entity.toAddress
import com.github.mattisonchao.node.Node
import com.github.mattisonchao.rpc.CustomController
import com.github.mattisonchao.rpc.Response
import java.io.Serializable

data class NodeRs(val success: Boolean, val leader: String) : Serializable

class NodeController : CustomController {
    override fun handleRequest(nodeContext: Node, request: Any): Response<*> {
        if (!nodeContext.isLeader())
            return Response(NodeRs(false, nodeContext.getLeader()?.toAddress ?: ""))
        val submit = nodeContext.submit(request)
        return Response(NodeRs(submit, ""))
    }
}