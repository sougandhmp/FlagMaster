package org.smp.flagmaster.ui.mapper

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Calendar

@DisplayName("ChallengeTimeMapper")
class ChallengeTimeMapperTest {

    private lateinit var mapper: ChallengeTimeMapper

    @BeforeEach
    fun setUp() {
        mapper = ChallengeTimeMapper()
    }

    // ── Field mapping ─────────────────────────────────────────────────────────

    @ParameterizedTest(name = "{0}")
    @MethodSource("fieldMappingCases")
    @DisplayName("maps digit pairs to correct hour, minute, second, and zeroed milliseconds")
    fun mapsDigitsToCorrectTimeFields(
        @Suppress("UNUSED_PARAMETER") description: String,
        digits: List<String>,
        expectedHour: Int,
        expectedMinute: Int,
        expectedSecond: Int,
    ) {
        val result = mapper(digits)
        assertAll(
            { assertEquals(expectedHour,   result.get(Calendar.HOUR_OF_DAY), "hour")   },
            { assertEquals(expectedMinute, result.get(Calendar.MINUTE),      "minute") },
            { assertEquals(expectedSecond, result.get(Calendar.SECOND),      "second") },
            { assertEquals(0,              result.get(Calendar.MILLISECOND), "millis") },
        )
    }

    // ── Day-advance logic ─────────────────────────────────────────────────────

    @ParameterizedTest(name = "{0}")
    @MethodSource("dayAdvanceCases")
    @DisplayName("schedules on the correct day (past → tomorrow, future → today)")
    fun schedulesOnCorrectDay(
        @Suppress("UNUSED_PARAMETER") description: String,
        digits: List<String>,
        expectedDayOffset: Int,
    ) {
        val result = mapper(digits)
        val expected = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, expectedDayOffset)
        }
        assertEquals(expected.get(Calendar.DAY_OF_YEAR), result.get(Calendar.DAY_OF_YEAR))
    }

    // ── Test data ─────────────────────────────────────────────────────────────

    companion object {
        @JvmStatic
        fun fieldMappingCases(): List<Arguments> = listOf(
            Arguments.of("typical time 14:30:45",       listOf("1","4","3","0","4","5"), 14, 30, 45),
            Arguments.of("midnight 00:00:00",            listOf("0","0","0","0","0","0"),  0,  0,  0),
            Arguments.of("end of day 23:59:59",          listOf("2","3","5","9","5","9"), 23, 59, 59),
            Arguments.of("boundary hour 23",             listOf("2","3","0","0","0","0"), 23,  0,  0),
            Arguments.of("boundary minute 59",           listOf("2","3","5","9","0","0"), 23, 59,  0),
            Arguments.of("boundary second 59",           listOf("2","3","0","0","5","9"), 23,  0, 59),
            Arguments.of("empty strings default to 0",   List(6) { "" },                  0,  0,  0),
            Arguments.of("non-numeric strings default to 0", listOf("a","b","c","d","e","f"), 0, 0, 0),
        )

        @JvmStatic
        fun dayAdvanceCases(): List<Arguments> = listOf(
            // 00:00:00 is always in the past → must be pushed to tomorrow
            Arguments.of("00:00:00 is always past → advances to tomorrow",
                listOf("0","0","0","0","0","0"), 1),
            // empty digits resolve to 00:00:00 → same result
            Arguments.of("empty digits → 00:00:00 → advances to tomorrow",
                List(6) { "" }, 1),
            // 23:59:59 is virtually always in the future → stays today
            Arguments.of("23:59:59 is always future → stays today",
                listOf("2","3","5","9","5","9"), 0),
        )
    }
}
