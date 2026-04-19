package org.smp.flagmaster.ui.auth

import com.google.firebase.auth.FirebaseUser

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: FirebaseUser) : AuthState
}
