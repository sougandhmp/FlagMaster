package org.smp.domain.usecase.challenge

import java.util.Calendar
import javax.inject.Inject

class GetChallengeTimeUseCase @Inject constructor() {

    operator fun invoke(digits: List<String>): Calendar {
        val hour = "${digits[0]}${digits[1]}".toIntOrNull() ?: 0
        val minute = "${digits[2]}${digits[3]}".toIntOrNull() ?: 0
        val second = "${digits[4]}${digits[5]}".toIntOrNull() ?: 0

        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}