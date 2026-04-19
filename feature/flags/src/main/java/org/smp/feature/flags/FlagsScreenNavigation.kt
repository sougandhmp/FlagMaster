package org.smp.feature.flags

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.smp.feature.auth.AuthState
import org.smp.feature.auth.AuthViewModel

@Serializable
data object FlagsChallengeRoute

@Composable
internal fun FlagsChallengeRouteContent(
    onProfileClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {}
) {
    val viewModel: FlagsChallengeViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val authUiState = authViewModel.uiState.collectAsStateWithLifecycle()
    val userPhotoUrl = (authUiState.value.authState as? AuthState.Authenticated)?.user?.photoUrl
    FlagsChallengeScreen(
        uiState = uiState.value,
        onAction = viewModel::onAction,
        userPhotoUrl = userPhotoUrl,
        onProfileClick = onProfileClick,
        onLeaderboardClick = onLeaderboardClick
    )
}

fun NavGraphBuilder.timeScheduleScreen(
    onProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    composable<FlagsChallengeRoute> {
        FlagsChallengeRouteContent(
            onProfileClick = onProfileClick,
            onLeaderboardClick = onLeaderboardClick
        )
    }
}
