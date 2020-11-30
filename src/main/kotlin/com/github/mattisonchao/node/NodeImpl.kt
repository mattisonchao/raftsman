package com.github.mattisonchao.node

import com.github.mattisonchao.dispathcher.ElectionTask
import com.github.mattisonchao.dispathcher.RafterNodeDispatcher
import com.github.mattisonchao.entity.*
import com.github.mattisonchao.option.NodeOptions
import com.github.mattisonchao.option.StorageOptions
import com.github.mattisonchao.rpc.*
import com.github.mattisonchao.storage.LogEntries
import com.github.mattisonchao.storage.RocksdbLogEntries
import com.github.mattisonchao.utils.CountDownClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

data class MetaData(@Volatile var role: NodeRole, @Volatile var voteFor: String, @Volatile var currentTerm: Long, @Volatile var commitIndex: Long, @Volatile var lastApplied: Long, val nextIndex: MutableMap<EndPoint, Long> = mutableMapOf(), val matchIndex: MutableMap<EndPoint, Long> = mutableMapOf())

class NodeImpl private constructor(private val self: EndPoint) : Node {

    private lateinit var storageOption: StorageOptions
    private lateinit var nodeOptions: NodeOptions

    private val metadata = MetaData(NodeRole.FOLLOWER, "", 0L, 0, 0)
    private val server = RafterServer(self.port, RafterController(this))
    private val dispatcher = RafterNodeDispatcher()
    private val parliamentMember = ParliamentMemberImpl(this)
    private val electionClock = CountDownClock(name = "electionClock")
    private var heartBeatClock = CountDownClock(name = "heartBeatClock")
    private val entries: LogEntries by lazy {
        RocksdbLogEntries(storageOption)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NodeImpl::class.java)
        fun create(host: String, port: Int): Node = NodeImpl(EndPoint(host, port))
    }


    override fun getElectionClock(): CountDownClock = electionClock
    override fun getCustomController(): CustomController = nodeOptions.controller

    override fun isLeader(): Boolean {
        return NodeManager.getInstance().leader == self
    }

    override fun getLeader(): EndPoint? = NodeManager.getInstance().leader

    override fun getMetaData(): MetaData = metadata
    override fun getEndPoint(): EndPoint = self
    override fun getLogEntries(): LogEntries = entries


    override fun startup() {
        if (server.getStatus() == ServerStatus.RUNNABLE) {
            logger.error("Server {} has been running!", self.toAddress)
            return
        }
        server.start()
        dispatcher.registerTask(listOf(ElectionTask(this)))
        dispatcher.startup()
        logger.info("Server ${self.toAddress}  startup")
    }

    override fun shutdown() {
        RafterClient.getInstance().shutdown()
        server.shutDown()
    }

    override suspend fun toBeLearner() {
        CountDownClock.checkAndCancel(heartBeatClock)
    }

    override suspend fun toBeLeader() {
        CountDownClock.checkAndCancel(heartBeatClock)
        logger.info("Server ${self.toAddress}  to be leader")
        metadata.role = NodeRole.LEADER
        NodeManager.getInstance().leader = self
        metadata.voteFor = ""
        NodeManager.getInstance().getAllPeers().forEach{
            metadata.nextIndex[it] = entries.getLastIndex() + 1
            metadata.matchIndex[it] = 0
        }
        val lock = Mutex()
        heartBeatClock = CountDownClock(name = "heartBeatClock")
        heartBeatClock.start {
            GlobalScope.launch(Dispatchers.IO) {
                NodeManager.getInstance().getAllPeers().forEach {
                    val aeArguments = AEArguments(metadata.currentTerm, self.toAddress, 0, 0, null, 0)
                    val request = Request(RafterRequestMagic.APPEND_ENTRIES.code, it.toAddress, aeArguments)
                    launch {
                        val res = RafterClient.getInstance().syncSend<AEResult>(request)
                        lock.withLock {
                            if (res != null && res.body.term > metadata.currentTerm)
                                toBeFollower(res.body.term)
                        }
                    }
                }
                heartBeatClock.resetHeartBeatTime()
                electionClock.resetHeartBeatTimeOut()
            }
        }
    }

    override suspend fun toBeCandidate() {
        CountDownClock.checkAndCancel(heartBeatClock)
        logger.info("Server ${self.toAddress}  to be candidate")
        metadata.role = NodeRole.CANDIDATE
        metadata.voteFor = self.toAddress
        metadata.currentTerm += 1
        val voteCounter = AtomicInteger()
        withContext(Dispatchers.IO) {
            NodeManager.getInstance().getAllPeers().forEach {
                launch {
                    val request = Request(RafterRequestMagic.VOTE.code, it.toAddress, RVArguments(metadata.currentTerm, self.toAddress, 0, 0))
                    val res = RafterClient.getInstance().syncSend<RVResults>(request)
                    if (res != null) {
                        val rvResult = res.body
                        when {
                            rvResult.voteGranted -> {
                                voteCounter.incrementAndGet()
                            }
                            rvResult.term > metadata.currentTerm -> metadata.currentTerm = rvResult.term
                        }
                    }
                }
            }
        }
        if (metadata.role == NodeRole.CANDIDATE) {
            when {
                voteCounter.get() >= (NodeManager.getInstance().getAllPeers().size + 1) / 2 -> toBeLeader()
                else -> metadata.voteFor = ""
            }
        }
    }

    override fun toBeFollower(term: Long) {
        CountDownClock.checkAndCancel(heartBeatClock)
        metadata.currentTerm = term
        metadata.voteFor = ""
        metadata.role = NodeRole.FOLLOWER
        electionClock.resetHeartBeatTimeOut()
    }

    override fun submit(proposal: Any): Boolean =
            parliamentMember.submit(proposal)

    override fun withStorageOptions(storageOption: StorageOptions): Node {
        this.storageOption = storageOption
        return this
    }

    override fun withNodeOptions(nodeOptions: NodeOptions): Node {
        this.nodeOptions = nodeOptions
        return this
    }
}
