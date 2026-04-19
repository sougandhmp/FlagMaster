package org.smp.feature.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginScreen(authViewModel: AuthViewModel) {
    composable<LoginRoute> {
        LoginScreen(authViewModel = authViewModel)
    }
}
