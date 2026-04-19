package org.smp.domain.usecase.auth

import org.smp.domain.repository.AuthRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.createAccount(email, password)
}
