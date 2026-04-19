package org.smp.feature.auth

data class AuthUiState(
    val authState: AuthState = AuthState.Loading,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0,
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
)
