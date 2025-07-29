package org.smp.flagmaster.ui

import org.smp.domain.model.Country
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer

data class ScheduleTimeUiState(
    val scheduledTime: String = "",
    val remainingTime: String = "",
    val digits: List<String> = List(6) { "" },
    val errorMessage: String? = null,
    val secondsToStart: Long = 0L,
    val challengeState: ChallengeState = ChallengeState.NOT_SCHEDULED,
    val questions: List<Question> = emptyList(),
    val questionIndex: Int = 0,
    val currentQuestion: Question? = null,
    val selectedOption: Country? = null,
    val answer: String?=null,
    val answerResult: AnswerResult? = null,
    val showProgress: Boolean = false,
    val progressDuration: Int = 10,
    val score: Int = 0,
    val answers: List<QuizAnswer> = emptyList(),
)

enum class AnswerResult{
    CORRECT, WRONG
}

enum class ChallengeState {
    NOT_SCHEDULED,
    SCHEDULED,
    COUNT_DOWN,
    IN_PROGRESS,
    COMPLETED
}

sealed class QuizState {
    object NotScheduled : QuizState()
    object Expired : QuizState()
    data class InProgress(
        val scheduledTime: String,
        val answers: List<QuizAnswer>,
        val score: Int,
        val questionIndex: Int
    ) : QuizState()
}
