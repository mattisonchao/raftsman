package com.github.mattisonchao.storage

import com.alipay.remoting.serialization.HessianSerializer
import com.github.mattisonchao.entity.LogEntry
import kotlinx.coroutines.sync.Mutex
import org.rocksdb.Options
import org.rocksdb.RocksDB
import org.rocksdb.RocksDBException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger


/**
 * Use RocksDB implements log entries.
 *
 * @author mattisonchao@gmail.com
 * @since 1.1.1
 * @see LogEntries
 */
class RocksdbLogEntries private constructor() : LogEntries {

    private val rocksDB: RocksDB = RocksDB.open(Options().setCreateIfMissing(true), "./rafter-logEntries")

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RocksdbLogEntries::class.java)
        private val lock = Mutex()
        private val LAST_KEY = "LAST_INDEX_KEY".toByteArray()
        private val INSTANCE = RocksdbLogEntries()
        private val serializer = HessianSerializer()
        fun getInstance() = INSTANCE
    }


    override fun add(logEntry: LogEntry) {
        try {
            lock.tryLock()
            logEntry.index = getLastIndex() + 1
            val serializedLogEntry = serializer.serialize(logEntry)
            rocksDB.put(logEntry.index.toString().toByteArray(), serializedLogEntry)
        } catch (e: RocksDBException) {
            logger.error("RocksDB log entries add new entry throw exception ${e.message}")
        } finally {
            updateLastIndex(logEntry.index!!)
            lock.unlock()
        }
    }

    override fun get(index: Long): LogEntry? {
        val byteLogEntry = rocksDB[index.toString().toByteArray()]
        return serializer.deserialize<LogEntry>(byteLogEntry, LogEntry::class.java.toString())
    }

    override fun getLastWithIndex(): Pair<Long, LogEntry?> {
        val lastIndex = getLastIndex()
        return lastIndex to get(getLastIndex())
    }

    override fun getLastIndex(): Long {
        val lastIndex = rocksDB[LAST_KEY]
        return when {
            lastIndex != null -> String(lastIndex).toLong()
            else -> 0L
        }
    }

    override fun removeFromToLast(from: Long) {
        val lastIndex = getLastIndex()
        val counter = AtomicInteger(0);
        try {
            lock.tryLock()
            for (i in from..lastIndex) {
                rocksDB.delete(i.toString().toByteArray())
                counter.incrementAndGet()
            }
        } catch (e: RocksDBException) {
            logger.error("RocksDB log entries remove range throw exception ${e.message}")
        } finally {
            updateLastIndex(lastIndex - counter.get())
            lock.unlock()
        }
    }

    private fun updateLastIndex(index: Long) =
            rocksDB.put(LAST_KEY, index.toString().toByteArray())
}

