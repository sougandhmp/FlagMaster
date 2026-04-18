package org.smp.data.asset_data_source.dto

import com.google.gson.annotations.SerializedName
import org.smp.data.database.model.CountryEntity
import org.smp.data.database.model.QuestionEntity

data class QuestionWrapper(
    val questions: List<QuestionDto>
)

data class QuestionDto(
    @SerializedName("answer_id") val answerId: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("countries") val countries: List<CountryDto>,
    @SerializedName("fact") val fact: String = "",
    @SerializedName("difficulty") val difficulty: String = "",
)

data class CountryDto(
    @SerializedName("country_name") val countryName: String,
    @SerializedName("country_code") val countryCode: String,
)

// ── Mapping functions ────────────────────────────────────────────────────────

fun QuestionDto.toQuestionEntity(): QuestionEntity = QuestionEntity(
    answerId = answerId,
    countryCode = countryCode,
    fact = fact,
    difficulty = difficulty,
)

fun QuestionDto.toCountryEntities(): List<CountryEntity> = countries.take(4).map {
    CountryEntity(
        code = it.countryCode,
        countryName = it.countryName,
        questionId = answerId,
    )
}
