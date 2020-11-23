package com.github.mattisonchao.consistence

import com.github.mattisonchao.entity.*
import com.github.mattisonchao.node.Node
import com.github.mattisonchao.node.NodeManager
import com.github.mattisonchao.node.NodeRole
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

    override fun handleRequestVote(arguments: RVArguments): RVResults {
        logger.info(" Receive vote request from ${arguments.candidateId}")
        if (arguments.term < metaData.currentTerm)
            return RVResults(metaData.currentTerm, false)
        metaData.role = NodeRole.FOLLOWER
        NodeManager.getInstance().leader = arguments.candidateId.toEndPoint
        metaData.currentTerm = arguments.term
        metaData.voteFor = arguments.candidateId
        return RVResults(metaData.currentTerm, true)
    }

    override fun handleAppendEntries(arguments: AEArguments): AEResult {
        logger.info(" Receive heart beat request from ${arguments.leaderId}")
        if (arguments.term < metaData.currentTerm)
            return AEResult(metaData.currentTerm, false)
        NodeManager.getInstance().leader = arguments.leaderId.toEndPoint
        node.toBeFollower(arguments.term)
        node.getElectionClock().resetHeartBeatTimeOut()
        return AEResult(metaData.currentTerm, true)
    }

}