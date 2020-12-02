package com.github.mattisonchao.entity

import java.io.Serializable

data class RVArguments(val term: Long, val candidateId: String, val lastLogIndex: Long, val lastLogTerm: Long) : Serializable

data class RVResults(val term: Long, val voteGranted: Boolean) : Serializable