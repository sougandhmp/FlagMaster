package org.smp.domain.usecase.answers

import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

/**
 * Use case to observe the quiz answers from the repository
 *
 * @param flagsRepository FlagsRepository
 */
class ObserveQuizAnswersUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {

    /**
     * Observe the quiz answers from the repository
     *
     * @return Flow<List<QuizAnswer>>
     */
    operator fun invoke() = flagsRepository.observeQuizAnswers()

}