package org.smp.flagmaster.ui.time_schedule

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TimeScheduleRoute

fun NavController.navigateToTimeScheduleScreen(navOptions: NavOptions? = null) {
    navigate(route =TimeScheduleRoute, navOptions)
}

fun NavGraphBuilder.timeScheduleScreen(
    onBack: () -> Unit = { },
) {
    composable<TimeScheduleRoute> {
        TimeScheduleRoute(
            onBack = onBack
        )
    }
}