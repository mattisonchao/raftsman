package com.github.mattisonchao.utils

import com.github.mattisonchao.exception.CancelledCountDownClockException
import com.github.mattisonchao.exception.InfiniteCountDownException
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext

class CountDownClock(@Volatile private var countDownTime: Long = 0L, val name: String = "") {
    private val delegateScope = CoroutineScope(coroutineContext)

    companion object {
        fun checkAndCancel(clock: CountDownClock) {
            if (clock.isActive()) clock.cancel()
        }
    }

    fun start(block: suspend () -> Unit) {
        if (!delegateScope.isActive)
            throw CancelledCountDownClockException(" This count down clock has been canceled, please recreate one.")
        delegateScope.launch {
            while (true) {
                if (countDownTime == 0L) {
                    try {
                        block.invoke()
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                }
                if (countDownTime < 0)
                    throw InfiniteCountDownException(" Infinite count down, maybe you forget reset clock . eg: reset(millisecond: Long) clock name is $name")
                countDownTime -= 1
                delay(1)
            }
        }
    }

    fun reset(millisecond: Long) {
        countDownTime = millisecond
    }

    fun resetHeartBeatTime() {
        countDownTime = 50L
    }

    fun resetElectionTimeOut() {
        countDownTime = 150L + (0..150).random()
    }

    fun resetHeartBeatTimeOut() {
        countDownTime = 5000
    }

    fun cancel() = delegateScope.cancel()

    fun isActive() = delegateScope.isActive
}
