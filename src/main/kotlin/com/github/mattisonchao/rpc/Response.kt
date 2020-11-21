package com.github.mattisonchao.rpc

import java.io.Serializable


/**
 * Rpc response wrapper
 *
 * @property body Specific response body
 * @constructor create new response by param
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 */
data class Response<T>(val body: T) : Serializable
