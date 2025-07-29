package org.smp.flagmaster.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TimeScheduleRoute

fun NavGraphBuilder.timeScheduleScreen() {
    composable<TimeScheduleRoute> {
        TimeScheduleRoute()
    }
}