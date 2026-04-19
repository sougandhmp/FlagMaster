package org.smp.feature.auth

sealed interface AuthAction {
    data class EmailChanged(val email: String) : AuthAction
    data class PasswordChanged(val password: String) : AuthAction
    data class TabSelected(val index: Int) : AuthAction
    data object GoogleSignIn : AuthAction
    data object TogglePasswordVisibility : AuthAction
    data object Submit : AuthAction
    data object SignOut : AuthAction
    data object ClearError : AuthAction
}
