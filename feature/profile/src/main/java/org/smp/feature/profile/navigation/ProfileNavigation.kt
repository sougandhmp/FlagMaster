package org.smp.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.smp.feature.auth.AuthViewModel
import org.smp.feature.profile.ProfileScreen
import org.smp.feature.profile.ProfileSetupScreen

@Serializable
data object ProfileRoute

@Serializable
data object ProfileSetupRoute

fun NavGraphBuilder.profileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileScreen(authViewModel = authViewModel, onBack = onBack)
    }
}

fun NavGraphBuilder.profileSetupScreen(authViewModel: AuthViewModel) {
    composable<ProfileSetupRoute> {
        ProfileSetupScreen(authViewModel = authViewModel)
    }
}
