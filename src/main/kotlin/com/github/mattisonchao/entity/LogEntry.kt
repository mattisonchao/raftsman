package com.github.mattisonchao.entity

import java.io.Serializable

data class LogEntry(var index: Long? = null, val term: Long, val command: Any) : Serializable