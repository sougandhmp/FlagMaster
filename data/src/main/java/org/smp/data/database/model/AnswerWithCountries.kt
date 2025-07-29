package org.smp.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import org.smp.domain.model.Country
import org.smp.domain.model.Question

data class AnswerWithCountries(
    @Embedded val answer: AnswerEntity,
    @Relation(
        parentColumn = "answerId",
        entityColumn = "answerOwnerId"
    )
    val countries: List<CountryEntity>
)

fun AnswerWithCountries.toQuestion(): Question = Question(
    answerId = answer.answerId.toString(),
    countryCode = answer.countryCode,
    options = countries.map { it.toCountry() }
)

private fun CountryEntity.toCountry(): Country = Country(
    name = countryName,
    id = id.toString()
)
