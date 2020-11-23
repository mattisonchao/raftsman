package com.github.mattisonchao.rpc

import com.alipay.remoting.exception.RemotingException
import com.alipay.remoting.rpc.RpcClient
import org.slf4j.LoggerFactory

/**
 * Rafter RPC client interface
 *
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 */
interface Client {
    /**
     * Sync send request message to target server and get specific type response.
     *
     * @param request the request wrapper to wrapper rpc message
     * @return  specific type response
     * @see Request
     * @see Response
     */
    fun <S> syncSend(request: Request<*>): Response<S>?

    fun shutdown()
}

/**
 * Rafter Client implement Client interface.
 *
 * @author mattisonchao@gmail.com
 * @see Client
 * @since 1.1.1
 */
class RafterClient private constructor() : Client {

    companion object {
        private val INSTANCE = RafterClient()
        private val logger = LoggerFactory.getLogger(RafterClient::class.java)
        fun getInstance() = INSTANCE
    }

    private val client = RpcClient()

    init {
        client.startup()
    }


    @Suppress("UNCHECKED_CAST")
    override fun <S> syncSend(request: Request<*>): Response<S>? {
        try {
            return client.invokeSync(request.target, request, 4000) as Response<S>
        } catch (e: RemotingException) {
            logger.info("rpc exception {}", e.message)
        }
        return null
    }

    override fun shutdown() {
        client.shutdown()
    }

}
