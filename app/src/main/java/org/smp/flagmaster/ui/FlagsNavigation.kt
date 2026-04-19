package org.smp.flagmaster.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.smp.feature.auth.AuthState
import org.smp.feature.auth.AuthViewModel
import org.smp.feature.auth.LoginRoute
import org.smp.feature.auth.loginScreen
import org.smp.feature.leaderboard.navigation.LeaderboardRoute
import org.smp.feature.leaderboard.navigation.leaderboardScreen
import org.smp.feature.profile.navigation.ProfileRoute
import org.smp.feature.profile.navigation.ProfileSetupRoute
import org.smp.feature.profile.navigation.profileScreen
import org.smp.feature.profile.navigation.profileSetupScreen
import org.smp.flagmaster.ui.sync.SyncViewModel

@Composable
fun FlagsNavigation() {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val syncViewModel = hiltViewModel<SyncViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> syncViewModel.pauseSync()
                Lifecycle.Event.ON_START -> syncViewModel.resumeSync()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val navController = rememberNavController()

    LaunchedEffect(authUiState.authState) {
        val currentRoute = navController.currentDestination?.route ?: ""
        val isAtAuthFlow = currentRoute.contains("LoginRoute") || currentRoute.contains("ProfileSetupRoute") || currentRoute.isEmpty()

        when (val state = authUiState.authState) {
            is AuthState.Authenticated -> {
                if (state.user.displayName.isNullOrBlank()) {
                    if (!currentRoute.contains("ProfileSetupRoute")) {
                        navController.navigate(ProfileSetupRoute) {
                            popUpTo<LoginRoute> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else if (isAtAuthFlow) {
                    navController.navigate(FlagsChallengeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                        popUpTo<ProfileSetupRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                if (!currentRoute.contains("LoginRoute")) {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            AuthState.Loading -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        loginScreen(authViewModel = authViewModel)
        profileSetupScreen(authViewModel = authViewModel)
        timeScheduleScreen(
            onProfileClick = { navController.navigate(ProfileRoute) },
            onLeaderboardClick = { navController.navigate(LeaderboardRoute) }
        )
        profileScreen(
            authViewModel = authViewModel,
            onBack = { navController.popBackStack() },
        )
        leaderboardScreen(
            onBack = { navController.popBackStack() }
        )
    }
}
