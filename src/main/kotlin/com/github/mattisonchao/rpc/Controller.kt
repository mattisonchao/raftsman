package com.github.mattisonchao.rpc

import com.alipay.remoting.BizContext

/**
 * Create sofa-bolt server controller to process request.
 *
 * @see RafterServer
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 */
interface Controller {
    /**
     * Request handler.
     *
     * @param bizCtx  basic info for biz
     * @param request request entity
     * @return specific type response
     */
    fun handleRequest(bizCtx: BizContext?, request: Request<*>): Response<*>?

}