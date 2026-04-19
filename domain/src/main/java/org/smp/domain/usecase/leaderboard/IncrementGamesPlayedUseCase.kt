package org.smp.domain.usecase.leaderboard

import org.smp.domain.model.UserStats
import org.smp.domain.repository.AuthRepository
import org.smp.domain.repository.LeaderboardRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class IncrementGamesPlayedUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke() {
        val user = authRepository.getCurrentUser() ?: return
        
        val currentStats = leaderboardRepository.observeUserStats(user.uid).firstOrNull() 
            ?: UserStats(uid = user.uid, displayName = user.displayName, photoUrl = user.photoUrl)

        val updatedStats = currentStats.copy(
            gamesPlayed = currentStats.gamesPlayed + 1,
            lastUpdate = System.currentTimeMillis()
        )

        leaderboardRepository.updateUserStats(updatedStats)
    }
}
