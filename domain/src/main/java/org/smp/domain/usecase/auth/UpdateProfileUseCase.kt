package org.smp.domain.usecase.auth

import kotlinx.coroutines.flow.firstOrNull
import org.smp.domain.model.UserStats
import org.smp.domain.repository.AuthRepository
import org.smp.domain.repository.LeaderboardRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val leaderboardRepository: LeaderboardRepository,
) {
    suspend operator fun invoke(displayName: String, photoUrl: String?) {
        authRepository.updateProfile(displayName, photoUrl)

        val user = authRepository.getCurrentUser() ?: return
        val currentStats = leaderboardRepository.observeUserStats(user.uid).firstOrNull()
            ?: UserStats(uid = user.uid, displayName = displayName, photoUrl = photoUrl)

        leaderboardRepository.updateUserStats(
            currentStats.copy(
                displayName = displayName,
                photoUrl = photoUrl,
                lastUpdate = System.currentTimeMillis()
            )
        )
    }
}
