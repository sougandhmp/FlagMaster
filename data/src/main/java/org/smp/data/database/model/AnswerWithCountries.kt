package org.smp.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import org.smp.domain.model.Country
import org.smp.domain.model.Question

data class QuestionWithOptions(
    @Embedded val question: QuestionEntity,
    @Relation(
        parentColumn = "answerId",      // QuestionEntity column name
        entityColumn = "questionId"  // CountryEntity column name
    )
    val options: List<CountryEntity>
)

fun QuestionWithOptions.toQuestion(): Question = Question(
    answerId = question.answerId,
    countryCode = question.countryCode,
    options = options.take(4).map { it.toCountry() },
    fact = question.fact,
)

private fun CountryEntity.toCountry(): Country = Country(
    name = countryName,
    code = code
)
