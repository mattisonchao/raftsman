package com.github.mattisonchao

import com.github.mattisonchao.node.RafterRequestMagic
import com.github.mattisonchao.rpc.RafterClient
import com.github.mattisonchao.rpc.Request

fun main() {
    val request = Request(RafterRequestMagic.CLIENT_REQUEST.code, "127.0.0.1:8886", "hello")
    val rsp = RafterClient.getInstance().syncSend<NodeRs>(request)
    val body = rsp?.body
    if (body != null){
        if (body.success) {
            println("success")
        } else {
            if (body.leader.isNotBlank()){
                val rs = RafterClient.getInstance().syncSend<NodeRs>( Request(RafterRequestMagic.CLIENT_REQUEST.code, body.leader, "hello"))
                println(rs)
            }
        }
   }
}