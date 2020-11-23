package com.github.mattisonchao.dispathcher

import com.github.mattisonchao.node.*

/**
 * Election Task to vote new leader when count down clock is complete.
 *
 * @since 1.1.1
 * @author mattisonchao@gmail.com
 */
class ElectionTask(private val node: Node) : Task {
    private val clock = node.getElectionClock()

    override suspend fun run() {
        clock.start {
            if (node.getMetaData().role == NodeRole.LEADER)
                return@start
            node.toBeCandidate()
            clock.resetElectionTimeOut()
        }
    }
}