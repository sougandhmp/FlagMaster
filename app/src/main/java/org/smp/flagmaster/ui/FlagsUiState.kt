package org.smp.flagmaster.ui

import org.smp.domain.model.Country
import org.smp.domain.model.DifficultyMode
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer

data class ScheduleTimeUiState(
    val scheduledTime: String = "",
    val remainingTime: String = "",
    val digits: List<String> = List(6) { "" },
    val errorMessage: String? = null,
    val challengeState: ChallengeState = ChallengeState.NOT_SCHEDULED,
    val questions: List<Question> = emptyList(),
    val questionIndex: Int = 0,
    val currentQuestion: Question? = null,
    val selectedOption: Country? = null,
    val answerResult: AnswerResult? = null,
    val score: Int = 0,
    val answers: List<QuizAnswer> = emptyList(),
    val showScheduler: Boolean = false,
    val difficultyMode: DifficultyMode = DifficultyMode.NORMAL,
    val showStats: Boolean = false,
    val questionCount: Int = 10,
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
