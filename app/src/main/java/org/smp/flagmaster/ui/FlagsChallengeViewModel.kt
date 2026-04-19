package org.smp.flagmaster.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer
import org.smp.domain.usecase.answers.ObserveQuizAnswersUseCase
import org.smp.domain.usecase.answers.SaveQuizAnswersUseCase
import org.smp.domain.usecase.challenge.ClearQuizAnswersAndTimeUseCase
import org.smp.domain.usecase.challenge.ObserveChallengeTimeUseCase
import org.smp.domain.usecase.challenge.SaveChallengeTimeUseCase
import org.smp.domain.usecase.leaderboard.IncrementGamesPlayedUseCase
import org.smp.domain.usecase.leaderboard.UpdateUserPointsUseCase
import org.smp.domain.usecase.questions.ObserveQuestionsUseCase
import org.smp.flagmaster.ui.mapper.ChallengeTimeMapper
import org.smp.flagmaster.ui.mapper.TimeSchedulerErrorMapper
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FlagsChallengeViewModel @Inject constructor(
    private val observeQuestionsUseCase: ObserveQuestionsUseCase,
    private val challengeTimeMapper: ChallengeTimeMapper,
    private val timeSchedulerErrorMapper: TimeSchedulerErrorMapper,
    private val observeChallengeTimeUseCase: ObserveChallengeTimeUseCase,
    private val observeQuizAnswersUseCase: ObserveQuizAnswersUseCase,
    private val saveChallengeTimeUseCase: SaveChallengeTimeUseCase,
    private val saveQuizAnswersUseCase: SaveQuizAnswersUseCase,
    private val clearQuizAnswersAndTimeUseCase: ClearQuizAnswersAndTimeUseCase,
    private val updateUserPointsUseCase: UpdateUserPointsUseCase,
    private val incrementGamesPlayedUseCase: IncrementGamesPlayedUseCase,
    private val soundManager: SoundManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleTimeUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var advanceJob: Job? = null
    private var allQuestions: List<Question> = emptyList()

    companion object {
        private const val QUIZ_TIMER_MS = 30_000L
        private const val QUIZ_INTERVAL_MS = 10_000L         // used for resume-time calculation only
        private const val FEEDBACK_DELAY_SELECTION_MS = 800L
        private const val FEEDBACK_DELAY_TIMEOUT_MS = 1500L
    }

    init {
        observeQuestions()
        observeSavedQuizAndTime()
    }

    private fun observeQuestions() {
        viewModelScope.launch {
            observeQuestionsUseCase()
                .collect { questions ->
                    if (questions.isNotEmpty()) {
                        allQuestions = questions
                        applyQuestionSelection(_uiState.value.questionCount)
                    }
                }
        }
    }

    private fun applyQuestionSelection(count: Int) {
        val selected = allQuestions.shuffled().take(count)
        _uiState.update {
            it.copy(
                questions = selected,
                currentQuestion = selected.getOrNull(it.questionIndex),
            )
        }
    }

    private fun observeSavedQuizAndTime() {
        viewModelScope.launch {
            combine(
                observeChallengeTimeUseCase(),
                observeQuizAnswersUseCase()
            ) { challengeTime, answers ->
                challengeTime to answers
            }.collect { (challengeTime, answers) ->
                val challenge = Calendar.getInstance().apply { timeInMillis = challengeTime }
                val calendar = getTimeString(challenge)
                val now = Calendar.getInstance()
                val timeForChallengeCompletion = 15 * (QUIZ_TIMER_MS + QUIZ_INTERVAL_MS)
                val completed =
                    now.timeInMillis > challenge.timeInMillis + timeForChallengeCompletion

                if (now.timeInMillis in challenge.timeInMillis..(challenge.timeInMillis + timeForChallengeCompletion)) {
                    val index =
                        ((now.timeInMillis - challenge.timeInMillis) / (QUIZ_TIMER_MS + QUIZ_INTERVAL_MS)).toInt()
                            .coerceIn(0..14)
                    _uiState.update {
                        it.copy(
                            scheduledTime = calendar,
                            challengeState = ChallengeState.IN_PROGRESS,
                            answers = answers,
                            score = answers.count { answer -> answer.isCorrect },
                            questionIndex = index,
                            currentQuestion = _uiState.value.questions.getOrNull(index),
                        )
                    }
                    startQuiz()
                } else if (completed) {
                    clearAnswers()
                }
            }
        }
    }

    fun onAction(action: FlagsScreenAction) {
        when (action) {
            is FlagsScreenAction.OnDigitChange -> {
                val updatedDigits =
                    _uiState.value.digits.toMutableList().also { it[action.index] = action.value }
                _uiState.update { it.copy(digits = updatedDigits) }
            }

            is FlagsScreenAction.OnSave -> {
                val error = timeSchedulerErrorMapper(_uiState.value.digits)
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error) }
                } else {
                    val challengeTime = challengeTimeMapper(_uiState.value.digits)
                    _uiState.update { it.copy(showScheduler = false) }
                    scheduleChallenge(challengeTime)
                }
            }

            is FlagsScreenAction.OnOptionSelected -> {
                // Ignore taps once an answer has already been evaluated
                if (_uiState.value.answerResult == null) {
                    _uiState.update { it.copy(selectedOption = action.option) }
                    evaluateAndAdvance(FEEDBACK_DELAY_SELECTION_MS)
                }
            }

            is FlagsScreenAction.StartQuiz -> startQuiz()

            is FlagsScreenAction.OnScheduleChallenge -> {
                _uiState.update { it.copy(showScheduler = true) }
            }

            is FlagsScreenAction.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            is FlagsScreenAction.OnDifficultySelected -> {
                _uiState.update { it.copy(difficultyMode = action.mode) }
            }

            is FlagsScreenAction.OnQuestionCountSelected -> {
                _uiState.update { it.copy(questionCount = action.count) }
                applyQuestionSelection(action.count)
            }

            is FlagsScreenAction.PlayAgain -> resetForNewGame()
            is FlagsScreenAction.GoHome -> resetForNewGame()
            is FlagsScreenAction.ShowStats -> _uiState.update { it.copy(showStats = true) }
            is FlagsScreenAction.HideStats -> _uiState.update { it.copy(showStats = false) }
            is FlagsScreenAction.SkipFact -> skipFact()
        }
    }

    private fun scheduleChallenge(challengeTime: Calendar) {
        val now = Calendar.getInstance()
        val millisUntilChallenge = challengeTime.timeInMillis - now.timeInMillis
        val millisUntil20Sec = millisUntilChallenge - 20_000L

        timerJob?.cancel()

        if (millisUntil20Sec <= 0) {
            _uiState.update { it.copy(errorMessage = "Selected time is too close or in the past!") }
            return
        }

        _uiState.update {
            it.copy(
                scheduledTime = getTimeString(challengeTime),
                challengeState = ChallengeState.SCHEDULED,
                errorMessage = null
            )
        }

        saveChallengeTime(challengeTime)

        // Sequential countdown phases in a single coroutine — no nested callbacks.
        timerJob = viewModelScope.launch {
            runCountdown(millisUntil20Sec)
            _uiState.update { it.copy(challengeState = ChallengeState.COUNT_DOWN) }
            runCountdown(20_000L)
            // Inline quiz start to avoid self-cancellation of the running timerJob.
            _uiState.update { it.copy(challengeState = ChallengeState.IN_PROGRESS) }
            runCountdown(_uiState.value.difficultyMode.timerMs)
            if (_uiState.value.answerResult == null) {
                evaluateAndAdvance(FEEDBACK_DELAY_TIMEOUT_MS)
            }
        }
    }

    private fun saveChallengeTime(challengeTime: Calendar) {
        viewModelScope.launch {
            runCatching { saveChallengeTimeUseCase(challengeTime.timeInMillis) }
                .onSuccess { Timber.d("Challenge time saved ${getTimeString(challengeTime)}") }
                .onFailure { Timber.e(it, "Failed to save challenge time") }
        }
    }

    // Counts down [duration] ms in 1-second ticks, updating remainingTime on each tick.
    private suspend fun runCountdown(duration: Long) {
        var remaining = duration
        _uiState.update { it.copy(remainingTime = formatTimeFromMillis(remaining)) }
        while (remaining > 0) {
            delay(1_000L)
            remaining = (remaining - 1_000L).coerceAtLeast(0L)
            _uiState.update { it.copy(remainingTime = formatTimeFromMillis(remaining)) }
        }
    }

    private fun formatTimeFromMillis(millis: Long): String {
        val totalSeconds = (millis / 1000).toInt().coerceAtLeast(0)
        return totalSeconds.toString()
    }

    private fun getTimeString(calendar: Calendar): String =
        String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )

    private fun startQuiz() {
        _uiState.update { it.copy(challengeState = ChallengeState.IN_PROGRESS) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            runCountdown(_uiState.value.difficultyMode.timerMs)
            // Timer expired — only evaluate if the user hasn't already picked an answer
            if (_uiState.value.answerResult == null) {
                evaluateAndAdvance(FEEDBACK_DELAY_TIMEOUT_MS)
            }
        }
    }

    /**
     * Evaluates the current answer (or marks it wrong if nothing was selected),
     * then advances to the next question after [feedbackDelayMs].
     *
     * Called immediately when the user picks an option (1 s delay) or when the
     * 30-second timer expires without a selection (2 s delay).
     */
    private fun evaluateAndAdvance(feedbackDelayMs: Long) {
        val question = _uiState.value.currentQuestion ?: return
        val selection = _uiState.value.selectedOption
        val currentIndex = _uiState.value.questionIndex

        timerJob?.cancel()

        val isCorrect = question.answerId == selection?.code
        val updatedAnswers = _uiState.value.answers.toMutableList().apply {
            removeAll { it.questionId == question.questionId }
            add(
                QuizAnswer(
                    questionId = question.questionId,
                    selectedOption = selection?.code.orEmpty(),
                    isCorrect = isCorrect
                )
            )
        }
        val newStreak = if (isCorrect) _uiState.value.currentStreak + 1 else 0

        val totalTicks = (feedbackDelayMs / 1000).toInt()
        val remainingTimeMs = 0L // TODO: Pass actual remaining time if needed for points
        
        _uiState.update {
            it.copy(
                answers = updatedAnswers,
                answerResult = if (isCorrect) AnswerResult.CORRECT else AnswerResult.WRONG,
                score = updatedAnswers.count { answer -> answer.isCorrect },
                currentStreak = newStreak,
                factCountdown = totalTicks,
            )
        }

        if (isCorrect) soundManager.playCorrect() else soundManager.playWrong()

        val isLast = currentIndex == _uiState.value.questions.lastIndex
        if (feedbackDelayMs < 1000L) {
             viewModelScope.launch {
                 delay(feedbackDelayMs)
                 moveToNextQuestionOrFinish(isLast = isLast, currentIndex = currentIndex)
             }
             return
        }

        viewModelScope.launch {
            runCatching {
                updateUserPointsUseCase(
                    isCorrect = isCorrect,
                    remainingTimeMs = remainingTimeMs,
                    totalTimeMs = _uiState.value.difficultyMode.timerMs,
                    currentStreak = newStreak,
                    difficultyMode = _uiState.value.difficultyMode
                )
            }.onFailure { Timber.e(it, "Failed to update user stats") }

            runCatching { saveQuizAnswersUseCase(updatedAnswers) }
                .onSuccess { Timber.d("Answers saved $updatedAnswers") }
                .onFailure { Timber.e(it, "Failed to save answers") }
        }

        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            for (remaining in totalTicks downTo 1) {
                _uiState.update { it.copy(factCountdown = remaining) }
                delay(1000L)
            }
            _uiState.update { it.copy(factCountdown = 0) }
            moveToNextQuestionOrFinish(isLast = isLast, currentIndex = currentIndex)
        }
    }

    private fun skipFact() {
        val state = _uiState.value
        if (state.answerResult == null) return
        advanceJob?.cancel()
        _uiState.update { it.copy(factCountdown = 0) }
        moveToNextQuestionOrFinish(
            isLast = state.questionIndex == state.questions.lastIndex,
            currentIndex = state.questionIndex
        )
    }

    private fun moveToNextQuestionOrFinish(isLast: Boolean, currentIndex: Int) {
        if (!isLast) {
            val nextIndex = currentIndex + 1
            val nextQuestion = _uiState.value.questions[nextIndex]
            _uiState.update {
                it.copy(
                    questionIndex = nextIndex,
                    currentQuestion = nextQuestion,
                    selectedOption = null,
                    answerResult = null,
                    factCountdown = 0,
                )
            }
            startQuiz()
        } else {
            clearAnswers()
            soundManager.playConfetti()
            viewModelScope.launch {
                runCatching { incrementGamesPlayedUseCase() }
                    .onFailure { Timber.e(it, "Failed to increment games played") }
            }
            _uiState.update {
                it.copy(
                    challengeState = ChallengeState.COMPLETED,
                    questionIndex = 0,
                    currentQuestion = null,
                    selectedOption = null,
                    answerResult = null,
                )
            }
        }
    }

    private fun resetForNewGame() {
        timerJob?.cancel()
        clearAnswers()
        val count = _uiState.value.questionCount
        _uiState.update { current ->
            ScheduleTimeUiState(
                questions = current.questions,
                difficultyMode = current.difficultyMode,
                questionCount = count,
            )
        }
        applyQuestionSelection(count)
    }

    private fun clearAnswers() {
        viewModelScope.launch {
            runCatching { clearQuizAnswersAndTimeUseCase() }
                .onFailure { Timber.e(it, "Failed to clear answers and challenge time") }
        }
    }

}
