package org.smp.domain.usecase.questions

import org.smp.domain.model.Question
import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

class GetAllQuestionsUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {
    suspend operator fun invoke(): List<Question> = flagsRepository.getAllQuestions()
}
