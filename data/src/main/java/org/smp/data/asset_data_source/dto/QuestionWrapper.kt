package org.smp.data.asset_data_source.dto

import com.google.gson.annotations.SerializedName
import org.smp.data.database.model.AnswerEntity
import org.smp.data.database.model.CountryEntity

data class QuestionWrapper(
    val questions: List<QuestionDto>
)

data class QuestionDto(
    @SerializedName("answer_id") val answerId: Int,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("countries") val countries: List<CountryDto>
)

data class CountryDto(
    @SerializedName("country_name") val countryName: String,
    val id: Int
)

fun QuestionDto.toQuestionEntity(): AnswerEntity = AnswerEntity(
    answerId = answerId,
    countryCode = countryCode,
)

fun QuestionDto.toCountryEntities() : List<CountryEntity> = countries.map {
    CountryEntity(
        id = it.id,
        countryName = it.countryName,
        answerOwnerId = answerId
    )
}