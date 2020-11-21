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
     * Start client to prepare send message.
     */
    fun start()

    /**
     * Sync send request message to target server and get specific type response.
     *
     * @param request the request wrapper to wrapper rpc message
     * @return  specific type response
     * @see Request
     * @see Response
     */
    fun <S> syncSend(request: Request<*>): Response<S>?

}

/**
 * Rafter Client implement Client interface.
 *
 * @author mattisonchao@gmail.com
 * @see Client
 * @since 1.1.1
 */
class RafterClient : Client {
    companion object {
        private val logger = LoggerFactory.getLogger(RafterClient::class.java)
    }

    private val client = RpcClient()

    override fun start() {
        client.startup()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <S> syncSend(request: Request<*>): Response<S>? {
        try {
            return client.invokeSync(request.target, request, 0) as Response<S>
        } catch (e: RemotingException) {
            logger.info("rpc exception {}", e.message)
        }
        return null
    }

}
