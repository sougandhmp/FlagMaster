package org.smp.domain.usecase.questions

import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

class ScheduleSyncUseCase @Inject constructor(
    private val flagsRepository: FlagsRepository
) {
    operator fun invoke() {
        flagsRepository.scheduleSync()
    }
}
