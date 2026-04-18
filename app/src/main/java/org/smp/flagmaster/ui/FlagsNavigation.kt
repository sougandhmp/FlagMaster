package org.smp.flagmaster.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.smp.flagmaster.ui.sync.SyncViewModel

@Composable
fun FlagsNavigation() {
    hiltViewModel<SyncViewModel>()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = FlagsChallengeRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        timeScheduleScreen()
    }
}