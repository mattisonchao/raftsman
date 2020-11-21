package com.github.mattisonchao.rpc

import com.alipay.remoting.AsyncContext
import com.alipay.remoting.BizContext
import com.alipay.remoting.rpc.RpcServer
import com.alipay.remoting.rpc.protocol.AbstractUserProcessor

/**
 * Rafter rpc server interface.
 *
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 */
interface Server {

    /**
     * Start server
     */
    fun start()

    /**
     * Shutdown server
     */
    fun shutDown()

    /**
     * Get server status
     *
     * @return server status
     * @see ServerStatus
     */
    fun getStatus(): ServerStatus
}

enum class ServerStatus {
    RUNNABLE,
    SHUTDOWN
}

class RafterServer(port: Int, controller: Controller) : Server {
    private val server = RpcServer(port, false, false)
    private var status: ServerStatus = ServerStatus.SHUTDOWN

    init {
        server.registerUserProcessor(object : AbstractUserProcessor<Request<*>>() {
            override fun handleRequest(bizCtx: BizContext?, request: Request<*>?): Response<*>? = request?.let { controller.handleRequest(bizCtx, request) }

            override fun interest(): String? = Request::class.qualifiedName

            override fun handleRequest(bizCtx: BizContext?, asyncCtx: AsyncContext?, request: Request<*>?) =
                    throw UnsupportedOperationException("unsupported handleRequest(bizCtx: BizContext?, asyncCtx: AsyncContext?, request: Request<*>?) ")
        })
    }

    override fun start() {
        server.startup()
        status = ServerStatus.RUNNABLE
    }

    override fun shutDown() {
        server.shutdown()
        status = ServerStatus.SHUTDOWN
    }

    override fun getStatus() = status

}
