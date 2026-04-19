package org.smp.feature.leaderboard.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.smp.feature.leaderboard.LeaderboardScreen
import org.smp.feature.leaderboard.LeaderboardViewModel

@Serializable
data object LeaderboardRoute

fun NavGraphBuilder.leaderboardScreen(onBack: () -> Unit) {
    composable<LeaderboardRoute> {
        val viewModel = hiltViewModel<LeaderboardViewModel>()
        LeaderboardScreen(viewModel = viewModel, onBack = onBack)
    }
}
