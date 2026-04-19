package org.smp.domain.usecase.auth

import org.smp.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String) = repo.signInWithGoogle(idToken)
}
