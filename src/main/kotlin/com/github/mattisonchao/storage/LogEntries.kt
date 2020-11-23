package com.github.mattisonchao.storage

import com.github.mattisonchao.entity.LogEntry

interface LogEntries {
    fun add(logEntry: LogEntry)

    fun get(index: Long): LogEntry?

    fun getLastWithIndex(): Pair<Long, LogEntry?>

    fun getLastIndex(): Long

    fun removeFromToLast(from: Long)
}
