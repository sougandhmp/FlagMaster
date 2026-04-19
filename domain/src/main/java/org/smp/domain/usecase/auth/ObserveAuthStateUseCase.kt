package org.smp.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.AuthUser
import org.smp.domain.repository.AuthRepository
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(private val repo: AuthRepository) {
    operator fun invoke(): Flow<AuthUser?> = repo.observeAuthState()
}
