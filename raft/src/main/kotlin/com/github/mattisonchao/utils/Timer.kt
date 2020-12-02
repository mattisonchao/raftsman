package com.github.mattisonchao.utils

import kotlinx.coroutines.delay

class TimeUtils {
    companion object {
        suspend fun setInterval(millisecond: Long, block: suspend () -> Unit) {
            while (true) {
                try {
                    block.invoke()
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
                delay(millisecond)
            }
        }
    }
}