package org.smp.domain.usecase.questions

import org.smp.domain.model.Question
import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

/**
 * Use case to get all questions from the repository
 */
class GetAllQuestionsUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {

    /**
     * Get all questions from the repository
     *
     * @return List of questions
     */
    suspend operator fun invoke(): List<Question> = flagsRepository.getAllQuestions()
}