package org.smp.domain.usecase.challenge

import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

class ClearQuizAnswersAndTimeUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {

    suspend operator fun invoke() {
        flagsRepository.clearQuizAnswersAndTime()
    }
}