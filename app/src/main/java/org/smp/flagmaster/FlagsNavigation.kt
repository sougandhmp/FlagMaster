package org.smp.flagmaster

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.smp.flagmaster.ui.time_schedule.TimeScheduleRoute
import org.smp.flagmaster.ui.time_schedule.timeScheduleScreen

@Composable
fun FlagsNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TimeScheduleRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        timeScheduleScreen(
            onBack = { navController.popBackStack() }
        )
    }
}