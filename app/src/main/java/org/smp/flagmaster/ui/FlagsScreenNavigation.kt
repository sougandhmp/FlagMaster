package org.smp.flagmaster.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object FlagsChallengeRoute

fun NavGraphBuilder.timeScheduleScreen(
    onProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    composable<FlagsChallengeRoute> {
        FlagsChallengeRoute(
            onProfileClick = onProfileClick,
            onLeaderboardClick = onLeaderboardClick
        )
    }
}
