package com.github.mattisonchao.node

import com.github.mattisonchao.entity.AEArguments
import com.github.mattisonchao.entity.AEResult
import com.github.mattisonchao.entity.LogEntry
import com.github.mattisonchao.entity.toAddress
import com.github.mattisonchao.rpc.RafterClient
import com.github.mattisonchao.rpc.Request
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class ParliamentMemberImpl(private val node: Node) : ParliamentMember {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ParliamentMemberImpl::class.java)
    }

    private val metaData = node.getMetaData()

    override fun submit(proposal: Any): Boolean {
        logger.info("node receive submit ,the proposal is", node.getEndPoint().toAddress, proposal)
        val logEntry = saveLogAndFillIndex(proposal)
        val successCounter = AtomicInteger(0)
        logger.info("node save proposal success!")
        replicationToPeers(logEntry, successCounter)
        logger.info("node replication to peer success!")

        updateSelfCommitIndex()

        return if (successCounter.get() >= ((NodeManager.getInstance().getAllPeers().size + 1) / 2)) {
            metaData.commitIndex = logEntry.index!!
            metaData.lastApplied = logEntry.index!!
            true
        } else {
            node.getLogEntries().removeFromToLast(logEntry.index!!)
            false
        }
    }

    private fun updateSelfCommitIndex() {
        val sortedMatchIndexList = metaData.matchIndex.values.sorted()
        val median = if (sortedMatchIndexList.size >= 2) sortedMatchIndexList.size / 2 else 0
        val commitIndex = sortedMatchIndexList[median]
        if (commitIndex > metaData.commitIndex) {
            val entry = node.getLogEntries().get(commitIndex)
            if (entry != null && entry.term == metaData.currentTerm)
                metaData.commitIndex = commitIndex
        }
    }

    private fun saveLogAndFillIndex(proposal: Any): LogEntry {
        val logEntry = LogEntry(term = metaData.currentTerm, command = proposal)
        node.getLogEntries().add(logEntry)
        return logEntry
    }

    private fun replicationToPeers(logEntry: LogEntry, successCounter: AtomicInteger) {
        runBlocking {
            logger.info("coroutine")
            NodeManager.getInstance().getAllPeers().forEach {
                val nextIndex: Long? = metaData.nextIndex[it]
                val logEntries = mutableListOf<LogEntry>()
                logger.info("1")
                if (logEntry.index!! >= nextIndex!!) {
                    for (i in nextIndex..logEntry.index!!) {
                        val entry = node.getLogEntries().get(i)
                        if (entry != null) logEntries.add(entry)
                    }
                } else {
                    logEntries.add(logEntry)
                }
                logger.info("2")
                val preEntry = node.getLogEntries().get(logEntries.first().index!! - 1)
                val param = AEArguments(metaData.currentTerm, node.getEndPoint().toAddress, preEntry?.index
                        ?: 0, preEntry?.term
                        ?: 0, logEntries, metaData.commitIndex)
                val request = Request(RafterRequestMagic.APPEND_ENTRIES.code, it.toAddress, param)
                launch {
                    logger.info("3")
                    val result = RafterClient.getInstance().syncSend<AEResult>(request) ?: return@launch
                    logger.info("4")
                    val aeResult = result.body
                    if (aeResult.success) {
                        successCounter.incrementAndGet()
                        metaData.nextIndex[it] = logEntry.index!! + 1
                        metaData.matchIndex[it] = logEntry.index!!
                    } else {
                        if (aeResult.term > metaData.currentTerm) {
                            node.toBeFollower(aeResult.term)
                        } else {
                            metaData.nextIndex[it] = nextIndex - 1
                        }
                    }
                }
            }
        }
    }
}

