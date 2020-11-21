package com.github.mattisonchao.rpc

import com.github.mattisonchao.node.RafterRequestTypeEnum
import java.io.Serializable

/**
 * Rpc request wrapper.
 *
 * @constructor create a request by param
 * @property type rafter request type.
 * @property target target server ip port
 * @property body request body
 * @see RafterRequestTypeEnum
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 */
data class Request<T>(val type: RafterRequestTypeEnum, val target: String, val body: T) : Serializable