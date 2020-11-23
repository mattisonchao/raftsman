package com.github.mattisonchao.consistence

import com.github.mattisonchao.entity.AEArguments
import com.github.mattisonchao.entity.AEResult
import com.github.mattisonchao.entity.RVArguments
import com.github.mattisonchao.entity.RVResults

/**
 * Raft consistence module,
 * use to leader election and log replication.
 *
 * @since 1.1.1
 * @author mattisonchao@gmail.com
 */
interface Consistence {
    /**
     * handle vote request , use to vote new leader.
     *
     * @param arguments RvArguments from raft paper
     * @return RvResults from raft paper
     */
    fun handleRequestVote(arguments: RVArguments): RVResults

    /**
     * handle append entries include log replication and heartbeat.
     *
     * @param arguments AEArguments from raft paper
     * @return AEResult from raft paper
     */
    fun handleAppendEntries(arguments: AEArguments): AEResult
}