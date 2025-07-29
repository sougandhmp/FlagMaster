package org.smp.domain.usecase.challenge

import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

/**
 * Use case to observe the challenge time from the repository
 *
 * @param flagsRepository FlagsRepository
 */
class ObserveChallengeTimeUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {

    /**
     * Observe the challenge time from the repository
     *
     * @return Flow<Long>
     */
    operator fun invoke() = flagsRepository.observeChallengeTime()

}