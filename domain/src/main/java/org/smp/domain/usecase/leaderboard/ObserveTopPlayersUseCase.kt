package org.smp.domain.usecase.leaderboard

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.UserStats
import org.smp.domain.repository.LeaderboardRepository
import javax.inject.Inject

class ObserveTopPlayersUseCase @Inject constructor(
    private val repository: LeaderboardRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<UserStats>> {
        return repository.observeTopPlayers(limit)
    }
}
