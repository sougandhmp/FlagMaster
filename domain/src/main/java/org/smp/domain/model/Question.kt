package org.smp.domain.model

data class Question(
    val answerId: String,
    val countryCode: String,
    val options: List<Country>,
)