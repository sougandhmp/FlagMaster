package org.smp.flagmaster.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.smp.domain.usecase.auth.CreateAccountUseCase
import org.smp.domain.usecase.auth.ObserveAuthStateUseCase
import org.smp.domain.usecase.auth.SignInWithEmailUseCase
import org.smp.domain.usecase.auth.SignOutUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val observeAuthState: ObserveAuthStateUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val handleGoogleSignInUseCase: HandleGoogleSignInUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthState().collect { user ->
                _uiState.update {
                    it.copy(authState = if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated)
                }
            }
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.EmailChanged -> _uiState.update { it.copy(email = action.email) }
            is AuthAction.PasswordChanged -> _uiState.update { it.copy(password = action.password) }
            is AuthAction.TabSelected -> _uiState.update { it.copy(selectedTab = action.index) }
            is AuthAction.TogglePasswordVisibility -> _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            is AuthAction.Submit -> submit()
            is AuthAction.GoogleSignIn -> launchAuthAction { handleGoogleSignInUseCase() }
            is AuthAction.SignOut -> signOutUseCase()
            is AuthAction.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun submit() {
        val state = _uiState.value
        launchAuthAction {
            if (state.selectedTab == 0) signInWithEmailUseCase(state.email, state.password)
            else createAccountUseCase(state.email, state.password)
        }
    }

    private fun launchAuthAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { action() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) }; Timber.e(e, "Auth action failed") }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
