package org.smp.domain.usecase.auth

import org.smp.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val repo: AuthRepository) {
    operator fun invoke() = repo.signOut()
}
