package org.smp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.UserStats

interface LeaderboardRepository {
    fun observeUserStats(uid: String): Flow<UserStats?>
    fun observeTopPlayers(limit: Int): Flow<List<UserStats>>
    suspend fun updateUserStats(stats: UserStats)
}
