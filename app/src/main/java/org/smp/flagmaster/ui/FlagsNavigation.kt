package org.smp.flagmaster.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun FlagsNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = FlagsChallengeRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        timeScheduleScreen()
    }
}