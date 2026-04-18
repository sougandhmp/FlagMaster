package org.smp.domain.usecase.questions

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.Question
import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

/**
 * Returns a Flow backed by the local Room cache.
 * Emits the current list immediately, then re-emits whenever Firebase refreshes the cache.
 */
class ObserveQuestionsUseCase @Inject constructor(
    private val repository: FlagsRepository
) {
    operator fun invoke(): Flow<List<Question>> = repository.observeAllQuestions()
}
