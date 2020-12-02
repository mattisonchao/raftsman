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
     * Two rules:
     * 1. Reply false if term < currentTerm (§5.1)
     * 2. If votedFor is null or candidateId, and candidate’s log is at
     *    least as up-to-date as receiver’s log, grant vote (§5.2, §5.4)
     *
     * @param arguments RvArguments from raft paper
     * @return RvResults from raft paper
     */
    fun handleRequestVote(arguments: RVArguments): RVResults

    /**
     * handle append entries include log replication and heartbeat.
     * Five rules:
     * 1. Reply false if term < currentTerm (§5.1)
     * 2. Reply false if log doesn’t contain an entry at prevLogIndex
     *    whose term matches prevLogTerm (§5.3)
     * 3. If an existing entry conflicts with a new one (same index
     *    but different terms), delete the existing entry and all that
     *    follow it (§5.3)
     * 4. Append any new entries not already in the log
     * 5. If leaderCommit > commitIndex, set commitIndex =
     *    min(leaderCommit, index of last new entry)
     *
     * @param arguments AEArguments from raft paper
     * @return AEResult from raft paper
     */
    fun handleAppendEntries(arguments: AEArguments): AEResult
}