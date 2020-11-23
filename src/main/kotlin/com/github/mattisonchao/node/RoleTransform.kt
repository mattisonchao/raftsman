package com.github.mattisonchao.node

interface RoleTransform {
    suspend fun toBeLearner()
    suspend fun toBeLeader()
    suspend fun toBeCandidate()
    fun toBeFollower(term: Long)
}