package org.smp.domain.model

data class Question(
    val answerId: String,
    val countryCode: String,
    val options: List<Country>,
) {
    // answerId doubles as the unique question identifier (each question is about one country)
    val questionId: String get() = answerId
}