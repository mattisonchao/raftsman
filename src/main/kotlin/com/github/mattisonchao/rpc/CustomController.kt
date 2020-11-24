package com.github.mattisonchao.rpc

interface CustomController {

    fun <R, S> handleRequest(request: R): S

}
