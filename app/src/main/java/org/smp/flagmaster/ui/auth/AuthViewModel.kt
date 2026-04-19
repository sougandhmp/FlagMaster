package org.smp.flagmaster.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _authState.value = if (firebaseAuth.currentUser != null) {
            AuthState.Authenticated(firebaseAuth.currentUser!!)
        } else {
            AuthState.Unauthenticated
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun signInWithEmail(email: String, password: String) =
        launchAuthAction("Email sign-in") { auth.signInWithEmailAndPassword(email, password).await() }

    fun createAccount(email: String, password: String) =
        launchAuthAction("Account creation") { auth.createUserWithEmailAndPassword(email, password).await() }

    fun signInWithGoogle(idToken: String) =
        launchAuthAction("Google sign-in") {
            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        }

    fun signOut() {
        auth.signOut()
    }

    fun clearError() {
        _error.value = null
    }

    private fun launchAuthAction(tag: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            runCatching { action() }
                .onFailure {
                    _error.value = it.message
                    Timber.e(it, "$tag failed")
                }
            _isLoading.value = false
        }
    }
}
