package org.smp.feature.auth

import org.smp.domain.usecase.auth.SignInWithGoogleUseCase
import javax.inject.Inject

class HandleGoogleSignInUseCase @Inject constructor(
    private val googleAuthProvider: GoogleAuthProvider,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) {
    suspend operator fun invoke() {
        val idToken = googleAuthProvider.getIdToken()
        signInWithGoogleUseCase(idToken)
    }
}
