package com.github.mattisonchao.node

import com.alipay.remoting.BizContext
import com.github.mattisonchao.consistence.RafterNodeConsistenceImpl
import com.github.mattisonchao.entity.AEArguments
import com.github.mattisonchao.entity.RVArguments
import com.github.mattisonchao.rpc.Controller
import com.github.mattisonchao.rpc.Request
import com.github.mattisonchao.rpc.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RafterController(private val node: Node) : Controller {


    private val raftService = RafterNodeConsistenceImpl(node)
    override fun handleRequest(bizCtx: BizContext?, request: Request<*>): Response<*>? =
            when (RafterRequestMagic.valueOf(request.type)) {
                RafterRequestMagic.VOTE -> Response(raftService.handleRequestVote(request.body as RVArguments))
                RafterRequestMagic.APPEND_ENTRIES -> Response(raftService.handleAppendEntries(request.body as AEArguments))
            }
}