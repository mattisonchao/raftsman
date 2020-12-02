package com.github.mattisonchao.storage

import com.github.mattisonchao.entity.LogEntry

/**
 * The log entries to save state.
 *
 * @since 1.1.1
 * @author mattisonchao@gmail.com
 */
interface LogEntries {
    /**
     * Add the log entry with lock
     *
     * @param logEntry logger entry
     */
    fun add(logEntry: LogEntry)

    /**
     * Get entry by index.
     *
     * @param index log entry index
     */
    fun get(index: Long): LogEntry?

    /**
     * Get last index and entry
     *
     * @return last index and entry
     */
    fun getLastWithIndex(): Pair<Long, LogEntry?>

    /**
     * Get last index
     * @return last index
     */
    fun getLastIndex(): Long

    /**
     * Remove log entry from specific index to last one
     * @param from specific index
     */
    fun removeFromToLast(from: Long)
}
