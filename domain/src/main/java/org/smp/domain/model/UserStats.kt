package org.smp.domain.model

data class UserStats(
    val uid: String,
    val displayName: String?,
    val photoUrl: String?,
    val totalPoints: Int = 0,
    val bestStreak: Int = 0,
    val gamesPlayed: Int = 0,
    val lastUpdate: Long = System.currentTimeMillis()
)
