package org.smp.flagmaster.ui.mapper

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.smp.flagmaster.R
import javax.inject.Inject

/**
 * Mapper for time scheduler errors.
 *
 * @param context Context for string resources
 * @return Error message if any, null otherwise
 */
class TimeSchedulerErrorMapper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Map time scheduler errors to error messages.
     *
     * @param digits List of digits representing the time
     * @return Error message if any, null otherwise
     */
    operator fun invoke(digits: List<String>): String? {
        val hourDigits = digits[0] + digits[1]
        val minuteDigits = digits[2] + digits[3]
        val secondDigits = digits[4] + digits[5]

        // Find missing fields
        val missing = buildList {
            if (digits[0].isEmpty() || digits[1].isEmpty()) add(context.getString(R.string.hour))
            if (digits[2].isEmpty() || digits[3].isEmpty()) add(context.getString(R.string.minute))
            if (digits[4].isEmpty() || digits[5].isEmpty()) add(context.getString(R.string.second))
        }
        if (missing.isNotEmpty()) {
            return context.getString(R.string.please_enter, missing.joinToString(" and "))
        }

        val hours = hourDigits.toIntOrNull()
        val minutes = minuteDigits.toIntOrNull()
        val seconds = secondDigits.toIntOrNull()


        return when {
            hours == null -> return context.getString(R.string.hour_must_be_two_digits_00_23)
            hours !in 0..23 -> return context.getString(
                R.string.hour_is_invalid_must_be_between_00_and_23,
                hourDigits
            )

            minutes == null -> return context.getString(R.string.minute_must_be_two_digits_00_59)
            minutes !in 0..59 -> return context.getString(
                R.string.minute_is_invalid_must_be_between_00_and_59,
                minuteDigits
            )

            seconds == null -> return context.getString(R.string.second_must_be_two_digits_00_59)
            seconds !in 0..59 -> return context.getString(
                R.string.second_is_invalid_must_be_between_00_and_59,
                secondDigits
            )

            else -> null
        }
    }
}