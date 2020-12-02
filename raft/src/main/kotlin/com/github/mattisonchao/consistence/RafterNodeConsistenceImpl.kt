package com.github.mattisonchao.consistence

import com.github.mattisonchao.entity.*
import com.github.mattisonchao.node.Node
import com.github.mattisonchao.node.NodeManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Rafter node consistence implementation.
 *
 * @see Consistence
 * @since 1.1.1
 * @author mattisonchao@gmail.com
 */
class RafterNodeConsistenceImpl(private val node: Node) : Consistence {

    private val metaData = node.getMetaData()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RafterNodeConsistenceImpl::class.java)
    }

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
    override fun handleRequestVote(arguments: RVArguments): RVResults {
        logger.debug(" Receive vote request from ${arguments.candidateId}")
        if (metaData.voteFor != "" && metaData.voteFor != arguments.candidateId)
            return RVResults(metaData.currentTerm, false)
        if (arguments.term < metaData.currentTerm)
            return RVResults(metaData.currentTerm, false)
        val (_, logEntry) = node.getLogEntries().getLastWithIndex()
        if (logEntry != null && (logEntry.term > arguments.lastLogTerm || logEntry.index!! > arguments.lastLogIndex)) // the log entry index must be not null
            return RVResults(metaData.currentTerm, false)
        node.toBeFollower(arguments.term)
        NodeManager.getInstance().leader = arguments.candidateId.toEndPoint
        metaData.voteFor = arguments.candidateId
        logger.debug(" Node accept vote , the candidate is { } the term is {}", arguments.candidateId, arguments.term)
        return RVResults(metaData.currentTerm, true)
    }

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
    override fun handleAppendEntries(arguments: AEArguments): AEResult {
        if (arguments.term < metaData.currentTerm)
            return AEResult(metaData.currentTerm, false)
        NodeManager.getInstance().leader = arguments.leaderId.toEndPoint
        node.toBeFollower(arguments.term)
        if (arguments.entries == null || arguments.entries.isEmpty()) {   // heart beat request
            logger.debug(" Receive heart beat request from ${arguments.leaderId}")
            return AEResult(metaData.currentTerm, true)
        }
        if (arguments.preLogIndex != 0L && node.getLogEntries().getLastIndex() != 0L) {
            val logEntry = node.getLogEntries().get(arguments.preLogIndex)
                    ?: run {
                        logger.debug(" The node don't have leader {} pre log index. ", arguments.leaderId)
                        return AEResult(metaData.currentTerm, false)
                    }

            if (logEntry.term != arguments.preLogTerm) {
                logger.debug(" The node term do not equal leader term, the leader term is {} , node term is {} ", arguments.term, metaData.currentTerm)
                return AEResult(metaData.currentTerm, false)
            }
        }
        val currentLogEntry = node.getLogEntries().get(arguments.preLogIndex + 1)
        if (currentLogEntry != null) {
            if (currentLogEntry.term != arguments.entries[0].term) {
                logger.debug(" The node entry term is not equal leader term , the leader entry term is {},node entry term is {}", arguments.entries[0].term, currentLogEntry.term)
                node.getLogEntries().removeFromToLast(arguments.preLogIndex + 1)
            } else {
                logger.debug(" The node has that entry.")
                return AEResult(metaData.currentTerm, true)
            }
        }
        arguments.entries.forEach {
            node.getLogEntries().add(it)
        }
        if (arguments.leaderCommit > metaData.commitIndex) {
            val commitIndex = arguments.leaderCommit.coerceAtMost(node.getLogEntries().getLastIndex())
            metaData.commitIndex = commitIndex
            metaData.lastApplied = commitIndex
        }
        logger.debug(" The node save entries success.")
        return AEResult(metaData.currentTerm, true)
    }
}