package org.smp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.AuthUser

interface AuthRepository {
    fun observeAuthState(): Flow<AuthUser?>
    fun getCurrentUser(): AuthUser?
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun createAccount(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun updateProfile(displayName: String, photoUrl: String?)
    fun signOut()
}
