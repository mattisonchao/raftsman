package com.github.mattisonchao.node

import com.github.mattisonchao.consistence.RafterNodeConsistenceImpl
import com.github.mattisonchao.entity.AEArguments
import com.github.mattisonchao.entity.RVArguments
import com.github.mattisonchao.exception.NoCustomControllerException
import com.github.mattisonchao.rpc.Controller
import com.github.mattisonchao.rpc.Request
import com.github.mattisonchao.rpc.Response

class RafterController(private val node: Node) : Controller {

    private val raftService = RafterNodeConsistenceImpl(node)
    override fun handleRequest(request: Request<*>): Response<*>? =
            when (RafterRequestMagic.valueOf(request.type)) {
                RafterRequestMagic.VOTE -> Response(raftService.handleRequestVote(request.body as RVArguments))
                RafterRequestMagic.APPEND_ENTRIES -> Response(raftService.handleAppendEntries(request.body as AEArguments))
                RafterRequestMagic.CLIENT_REQUEST -> node.getCustomController()?.handleRequest(request.body)
                        ?: throw NoCustomControllerException("Can't not found custom controller, do you set it in node configuration?")
            }
}