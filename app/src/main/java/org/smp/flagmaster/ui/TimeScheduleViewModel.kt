package org.smp.flagmaster.ui

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.smp.domain.model.QuizAnswer
import org.smp.domain.usecase.answers.ObserveQuizAnswersUseCase
import org.smp.domain.usecase.answers.SaveQuizAnswersUseCase
import org.smp.domain.usecase.challenge.ClearQuizAnswersAndTimeUseCase
import org.smp.domain.usecase.challenge.GetChallengeTimeUseCase
import org.smp.domain.usecase.challenge.ObserveChallengeTimeUseCase
import org.smp.domain.usecase.challenge.SaveChallengeTimeUseCase
import org.smp.domain.usecase.questions.GetAllQuestionsUseCase
import org.smp.domain.usecase.questions.SeedQuestionsUseCase
import org.smp.flagmaster.ui.mapper.TimeSchedulerErrorMapper
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TimeScheduleViewModel @Inject constructor(
    private val seedQuestionsUseCase: SeedQuestionsUseCase,
    private val getAllQuestionsUseCase: GetAllQuestionsUseCase,
    private val timeSchedulerErrorMapper: TimeSchedulerErrorMapper,
    private val observeChallengeTimeUseCase: ObserveChallengeTimeUseCase,
    private val observeQuizAnswersUseCase: ObserveQuizAnswersUseCase,
    private val saveChallengeTimeUseCase: SaveChallengeTimeUseCase,
    private val saveQuizAnswersUseCase: SaveQuizAnswersUseCase,
    private val clearQuizAnswersAndTimeUseCase: ClearQuizAnswersAndTimeUseCase,
    private val getChallengeTimeUseCase: GetChallengeTimeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleTimeUiState())
    val uiState = _uiState.asStateFlow()

    private var activeTimer: CountDownTimer? = null

    companion object {
        private const val QUIZ_TIMER_MS = 30_000L
        private const val QUIZ_INTERVAL_MS = 10_000L
    }

    init {
        seedQuestions()
        getAllQuestions()
        observeSavedQuizAndTime()
    }

    fun observeSavedQuizAndTime() {
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
                    Calendar.getInstance().timeInMillis > challenge.timeInMillis + timeForChallengeCompletion

                if (now.timeInMillis in challenge.timeInMillis..(challenge.timeInMillis + timeForChallengeCompletion)) {
                    val index =
                        ((now.timeInMillis - challenge.timeInMillis) / (QUIZ_TIMER_MS + QUIZ_INTERVAL_MS)).toInt()
                            .coerceIn(0..14)
                    _uiState.update {
                        it.copy(
                            scheduledTime = calendar,
                            challengeState = ChallengeState.IN_PROGRESS,
                            answers = answers,
                            score = answers.count { it.isCorrect },
                            questionIndex = index,
                            currentQuestion = uiState.value.questions.getOrNull(index),
                        )
                    }
                    startQuiz()
                } else if(completed){
                    clearAnswers()
                }
            }
        }
    }


    fun onAction(action: ScheduleScreenAction) {
        when (action) {
            is ScheduleScreenAction.OnDigitChange -> {
                val updatedDigits =
                    _uiState.value.digits.toMutableList().also { it[action.index] = action.value }
                _uiState.update { it.copy(digits = updatedDigits) }
            }

            is ScheduleScreenAction.OnSave -> {
                val error = timeSchedulerErrorMapper(_uiState.value.digits)
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error) }
                } else {
                    val challengeTime = getChallengeTime()
                    scheduleChallenge(challengeTime)
                }
            }

            is ScheduleScreenAction.OnOptionSelected -> {
                _uiState.update { it.copy(selectedOption = action.option) }
            }
        }
    }

    private fun scheduleChallenge(challengeTime: Calendar) {
        val now = Calendar.getInstance()
        val millisUntilChallenge = challengeTime.timeInMillis - now.timeInMillis
        val millisUntil20Sec = millisUntilChallenge - 20_000L

        activeTimer?.cancel()

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

        startCountdown(millisUntil20Sec) {
            _uiState.update { it.copy(challengeState = ChallengeState.COUNT_DOWN) }
            startCountdown(20_000L) {
                startQuiz()
                _uiState.update { it.copy(challengeState = ChallengeState.IN_PROGRESS) }
            }
        }
    }

    private fun saveChallengeTime(challengeTime: Calendar) {
        viewModelScope.launch {
            runCatching { saveChallengeTimeUseCase(challengeTime.timeInMillis) }.onSuccess {
                Timber.d(
                    "Challenge time saved ${getTimeString(challengeTime)}"
                )
            }.onFailure { Timber.e(it, "Failed to save challenge time") }
        }
    }

    private fun startCountdown(duration: Long, onFinish: () -> Unit) {
        _uiState.update { it.copy(remainingTime = formatTimeFromMillis(duration)) }
        activeTimer?.cancel()
        activeTimer = object : CountDownTimer(duration, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.update { it.copy(remainingTime = formatTimeFromMillis(millisUntilFinished)) }
            }

            override fun onFinish() {
                onFinish()
            }
        }.also { it.start() }
    }

    private fun formatTimeFromMillis(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = (totalSeconds / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun getChallengeTime() =
        runCatching { getChallengeTimeUseCase(_uiState.value.digits) }.getOrDefault(Calendar.getInstance())

    private fun getTimeString(calendar: Calendar): String =
        String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )

    private fun getAllQuestions() {
        viewModelScope.launch {
            runCatching { getAllQuestionsUseCase() }
                .onSuccess { questions ->
                    _uiState.update {
                        it.copy(
                            questions = questions,
                            currentQuestion = questions[it.questionIndex],
                            answer = questions[it.questionIndex].answerId
                        )
                    }
                }.onFailure {
                    Timber.e(it, "Error fetching questions")
                }
        }
    }

    private fun startQuiz() {
        val currentIndex = _uiState.value.questionIndex
        startCountdown(QUIZ_TIMER_MS) {
            val question = _uiState.value.currentQuestion
            val selection = _uiState.value.selectedOption

            if (question != null && selection != null) {
                val isCorrect = question.answerId == selection.id
                val updatedAnswers = _uiState.value.answers.toMutableList().apply {
                    removeAll { it.questionId == question.answerId }
                    add(
                        QuizAnswer(
                            questionId = question.answerId,
                            selectedOption = selection.id,
                            isCorrect = isCorrect
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        answers = updatedAnswers,
                        answerResult = if (isCorrect) AnswerResult.CORRECT else AnswerResult.WRONG,
                        score = updatedAnswers.count { it.isCorrect }
                    )
                }

                viewModelScope.launch {
                    runCatching {
                        saveQuizAnswersUseCase(updatedAnswers)
                    }.onSuccess {
                        Timber.d("Answers saved $updatedAnswers")
                    }.onFailure {
                        Timber.e(it, "Failed to save answers")
                    }
                }
            }

            moveToNextQuestionOrFinish(
                isLast = currentIndex == _uiState.value.questions.lastIndex,
                currentIndex = currentIndex
            )
        }
    }

    private fun moveToNextQuestionOrFinish(isLast: Boolean, currentIndex: Int) {
        if (!isLast) {
            _uiState.update { it.copy(showProgress = true) }
            viewModelScope.launch {
                delay(QUIZ_INTERVAL_MS)
                val nextIndex = currentIndex + 1
                val nextQuestion = _uiState.value.questions[nextIndex]
                _uiState.update {
                    it.copy(
                        showProgress = false,
                        questionIndex = nextIndex,
                        currentQuestion = nextQuestion,
                        answer = nextQuestion.answerId,
                        selectedOption = null,
                        answerResult = null
                    )
                }
                startQuiz()
            }
        } else {
            clearAnswers()
            _uiState.update {
                it.copy(
                    challengeState = ChallengeState.COMPLETED,
                    questionIndex = 0,
                    currentQuestion = null,
                    selectedOption = null,
                    answerResult = null
                )
            }
        }
    }

    private fun clearAnswers() {
        viewModelScope.launch {
            runCatching { clearQuizAnswersAndTimeUseCase() }
                .onFailure { Timber.e(it, "Failed to clear answers and challenge time") }
        }
    }

    private fun seedQuestions() {
        viewModelScope.launch {
            runCatching { seedQuestionsUseCase() }
                .onSuccess { Timber.d("Questions seeded") }
                .onFailure { Timber.e(it, "Seeding failed") }
        }
    }

    override fun onCleared() {
        activeTimer?.cancel()
        super.onCleared()
    }
}
