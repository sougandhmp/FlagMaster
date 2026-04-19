package org.smp.feature.auth

import org.smp.domain.model.AuthUser

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: AuthUser) : AuthState
}
