package org.smp.flagmaster.ui.auth

interface GoogleAuthProvider {
    suspend fun getIdToken(): String
}
