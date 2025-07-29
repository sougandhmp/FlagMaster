package org.smp.domain.model

data class QuizAnswer(
    val questionId: String,
    val selectedOption: String,
    val isCorrect: Boolean
)
