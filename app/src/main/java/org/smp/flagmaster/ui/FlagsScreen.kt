package org.smp.flagmaster.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.components.ChallengeScheduledView
import org.smp.flagmaster.ui.components.CircularLoading
import org.smp.flagmaster.ui.components.CountDownView
import org.smp.flagmaster.ui.components.FlagsChallengeHeader
import org.smp.flagmaster.ui.components.GameOverScreen
import org.smp.flagmaster.ui.components.QuestionScreen
import org.smp.flagmaster.ui.components.StartChallengeScreen
import org.smp.flagmaster.ui.theme.FlagMasterTheme

private val GradientStart = Color(0xFF667EEA)
private val GradientEnd = Color(0xFF764BA2)

@Composable
fun FlagsChallengeRoute(viewModel: FlagsChallengeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FlagsChallengeScreen(uiState = uiState, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlagsChallengeScreen(
    uiState: ScheduleTimeUiState,
    onAction: (FlagsScreenAction) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onAction(FlagsScreenAction.ClearError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd))
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            },
            topBar = {
                if (uiState.challengeState == ChallengeState.IN_PROGRESS) {
                    FlagsChallengeHeader(
                        time = uiState.remainingTime,
                        pageCount = "${uiState.score}/${uiState.questions.size.coerceAtLeast(1)}"
                    )
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = uiState.challengeState,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInVertically { it / 6 }) togetherWith
                            fadeOut(tween(200))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "challengeState"
            ) { state ->
                when (state) {
                    ChallengeState.NOT_SCHEDULED -> StartChallengeScreen(
                        uiState = uiState,
                        onAction = onAction
                    )

                    ChallengeState.SCHEDULED ->
                        ChallengeScheduledView(scheduledTime = uiState.scheduledTime)

                    ChallengeState.COUNT_DOWN ->
                        CountDownView(remainingTime = uiState.remainingTime)

                    ChallengeState.IN_PROGRESS -> uiState.currentQuestion?.let { currentQuestion ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            QuestionScreen(
                                modifier = Modifier.weight(1f),
                                question = currentQuestion,
                                questionNumber = uiState.questionIndex + 1,
                                totalQuestions = uiState.questions.size.coerceAtLeast(1),
                                selectedAnswer = uiState.selectedOption?.name,
                                showResult = uiState.answerResult != null,
                                remainingTime = uiState.remainingTime,
                                answerResult = uiState.answerResult,
                                streak = uiState.streak,
                                onAnswerSelected = {
                                    onAction(FlagsScreenAction.OnOptionSelected(it))
                                },
                                onNextQuestion = {}
                            )
                            AnimatedVisibility(
                                visible = uiState.showProgress && uiState.progressDuration > 0,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = slideOutVertically { it } + fadeOut()
                            ) {
                                IntervalBar(uiState.progressDuration)
                            }
                        }
                    }

                    ChallengeState.COMPLETED -> GameOverScreen(
                        score = uiState.score,
                        totalQuestions = uiState.questions.size.coerceAtLeast(1)
                    )
                }
            }
        }
    }
}

@Composable
private fun IntervalBar(timeInSeconds: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f),
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.next_question_available_in),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(12.dp))
            CircularLoading(timeInSeconds = timeInSeconds)
        }
    }
}

@Composable
@Preview
fun TimeSchedulePreview() {
    FlagMasterTheme {
        FlagsChallengeScreen(
            uiState = ScheduleTimeUiState(),
            onAction = {}
        )
    }
}
