package com.github.mattisonchao.entity

import java.io.Serializable

data class AEArguments(val term: Long, val leaderId: String, val preLogIndex: Long, val preLogTerm: Long, val entries: List<LogEntry>? = null, val leaderCommit: Long) : Serializable

data class AEResult(val term: Long, val success: Boolean) : Serializable