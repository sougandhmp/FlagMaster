package org.smp.flagmaster.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.smp.flagmaster.ui.auth.AuthState
import org.smp.flagmaster.ui.auth.AuthViewModel
import org.smp.flagmaster.ui.auth.LoginRoute
import org.smp.flagmaster.ui.auth.ProfileRoute
import org.smp.flagmaster.ui.auth.loginScreen
import org.smp.flagmaster.ui.auth.profileScreen
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
        when (authUiState.authState) {
            is AuthState.Authenticated -> navController.navigate(FlagsChallengeRoute) {
                popUpTo<LoginRoute> { inclusive = true }
                launchSingleTop = true
            }
            is AuthState.Unauthenticated -> navController.navigate(LoginRoute) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
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
        timeScheduleScreen(
            onProfileClick = { navController.navigate(ProfileRoute) }
        )
        profileScreen(
            authViewModel = authViewModel,
            onBack = { navController.popBackStack() },
        )
    }
}
