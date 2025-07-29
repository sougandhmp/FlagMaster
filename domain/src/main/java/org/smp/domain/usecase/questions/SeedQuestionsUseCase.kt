package org.smp.domain.usecase.questions

import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

class SeedQuestionsUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {
    suspend operator fun invoke() {
        flagsRepository.seedQuestionsFromAsset()
    }
}