package com.github.mattisonchao.node

import com.github.mattisonchao.entity.AEArguments
import com.github.mattisonchao.entity.AEResult
import com.github.mattisonchao.entity.LogEntry
import com.github.mattisonchao.entity.toAddress
import com.github.mattisonchao.rpc.RafterClient
import com.github.mattisonchao.rpc.Request
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

class ParliamentMemberImpl(private val node: Node) : ParliamentMember {
    private val metaData = node.getMetaData()

    override fun submit(proposal: Any): Boolean {
        val logEntry = saveLogAndFillIndex(proposal)
        val successCounter = AtomicInteger(0)

        replicationToPeers(logEntry, successCounter)

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
            NodeManager.getInstance().getAllPeers().forEach {
                val nextIndex: Long? = metaData.nextIndex[it]
                val logEntries = mutableListOf<LogEntry>()
                if (logEntry.index!! >= nextIndex!!) {
                    for (i in nextIndex..logEntry.index!!) {
                        val entry = node.getLogEntries().get(i)
                        if (entry != null) logEntries.add(entry)
                    }
                } else {
                    logEntries.add(logEntry)
                }
                val preEntry = node.getLogEntries().get(logEntries.first().index!! - 1)
                val param = AEArguments(metaData.currentTerm, node.getEndPoint().toAddress, preEntry?.index
                        ?: 0, preEntry?.term
                        ?: 0, logEntries, metaData.commitIndex)
                val request = Request(RafterRequestMagic.APPEND_ENTRIES.code, it.toAddress, param)
                launch {
                    val result = RafterClient.getInstance().syncSend<AEResult>(request) ?: return@launch
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
