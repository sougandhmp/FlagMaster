package org.smp.flagmaster.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.smp.flagmaster.ui.sync.SyncViewModel

@Composable
fun FlagsNavigation() {
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
    NavHost(
        navController = navController,
        startDestination = FlagsChallengeRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        timeScheduleScreen()
    }
}