package org.smp.feature.auth

interface GoogleAuthProvider {
    suspend fun getIdToken(): String
}
