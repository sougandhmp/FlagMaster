package org.smp.feature.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object ProfileRoute

fun NavGraphBuilder.loginScreen(authViewModel: AuthViewModel) {
    composable<LoginRoute> {
        LoginScreen(authViewModel = authViewModel)
    }
}

fun NavGraphBuilder.profileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileScreen(authViewModel = authViewModel, onBack = onBack)
    }
}
