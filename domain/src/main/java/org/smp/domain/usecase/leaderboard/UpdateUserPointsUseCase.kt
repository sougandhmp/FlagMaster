package org.smp.domain.usecase.leaderboard

import org.smp.domain.model.DifficultyMode
import org.smp.domain.model.UserStats
import org.smp.domain.repository.AuthRepository
import org.smp.domain.repository.LeaderboardRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateUserPointsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val leaderboardRepository: LeaderboardRepository
) {
    /**
     * Updates user points based on a single question result.
     * Points formula: 
     * Base: 10 points for correct answer
     * Time Bonus: (Remaining Time / Total Time) * 10
     * Streak Bonus: Current Streak * 2
     * Difficulty Multiplier: Easy=1x, Normal=1.5x, Hard=2x
     */
    suspend operator fun invoke(
        isCorrect: Boolean,
        remainingTimeMs: Long,
        totalTimeMs: Long,
        currentStreak: Int,
        difficultyMode: DifficultyMode
    ) {
        val user = authRepository.getCurrentUser() ?: return
        
        val currentStats = leaderboardRepository.observeUserStats(user.uid).firstOrNull() 
            ?: UserStats(uid = user.uid, displayName = user.displayName, photoUrl = user.photoUrl)

        if (!isCorrect) {
            leaderboardRepository.updateUserStats(
                currentStats.copy(
                    lastUpdate = System.currentTimeMillis()
                )
            )
            return
        }

        val basePoints = 10
        val timeBonus = (remainingTimeMs.toDouble() / totalTimeMs.toDouble() * 10).toInt()
        val streakBonus = currentStreak * 2
        
        val multiplier = when (difficultyMode) {
            DifficultyMode.EASY -> 1.0
            DifficultyMode.NORMAL -> 1.5
            DifficultyMode.HARD -> 2.0
        }

        val pointsEarned = ((basePoints + timeBonus + streakBonus) * multiplier).toInt()

        val updatedStats = currentStats.copy(
            totalPoints = currentStats.totalPoints + pointsEarned,
            bestStreak = maxOf(currentStats.bestStreak, currentStreak),
            lastUpdate = System.currentTimeMillis()
        )

        leaderboardRepository.updateUserStats(updatedStats)
    }
}
