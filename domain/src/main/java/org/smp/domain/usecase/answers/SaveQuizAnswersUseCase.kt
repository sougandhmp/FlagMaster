package org.smp.domain.usecase.answers

import org.smp.domain.model.QuizAnswer
import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

/**
 * Use case to save the quiz answers to the repository
 *
 * @param flagsRepository FlagsRepository
 */
class SaveQuizAnswersUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {

    /**
     * Save the quiz answers to the repository
     *
     * @param answers List<QuizAnswer>
     */
    suspend operator fun invoke(answers: List<QuizAnswer>) {
        flagsRepository.saveQuizAnswers(answers)
    }

}