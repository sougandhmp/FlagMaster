package org.smp.flagmaster.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.components.ChallengeScheduledView
import org.smp.flagmaster.ui.components.CircularLoading
import org.smp.flagmaster.ui.components.CountDownView
import org.smp.flagmaster.ui.components.GameOverScreen
import org.smp.flagmaster.ui.components.QuestionScreen
import org.smp.flagmaster.ui.components.StartChallengeScreen
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@Composable
fun FlagsChallengeRoute(
    viewModel: FlagsChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FlagsChallengeScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlagsChallengeScreen(
    uiState: ScheduleTimeUiState,
    onAction: (FlagsScreenAction) -> Unit = {},
) {


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    )
                )
            ),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2)
                        )
                    )
                )
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.challengeState) {
                    ChallengeState.NOT_SCHEDULED -> StartChallengeScreen(
                        startChallenge = { onAction(FlagsScreenAction.StartQuiz) }
                    )

                    ChallengeState.SCHEDULED ->
                        ChallengeScheduledView(scheduledTime = uiState.scheduledTime)

                    ChallengeState.COUNT_DOWN ->
                        CountDownView(remainingTime = uiState.remainingTime)

                    ChallengeState.IN_PROGRESS -> {
                        QuestionScreen(
                            question = uiState.currentQuestion!!,
                            questionNumber = uiState.progressDuration,
                            totalQuestions = uiState.questions.size,
                            selectedAnswer = uiState.selectedOption?.name,
                            showResult = uiState.answerResult != null,
                            onAnswerSelected = {
                                onAction(FlagsScreenAction.OnOptionSelected(it))
                            },
                            onNextQuestion = {
                            }
                        )
//                        ChallengeView(
//                            flagCountryCode = uiState.currentQuestion?.countryCode ?: "in",
//                            options = uiState.currentQuestion?.options.orEmpty(),
//                            onOptionSelected = {
//                                onAction(FlagsScreenAction.OnOptionSelected(it))
//                            },
//                            selected = uiState.selectedOption,
//                            result = uiState.answerResult,
//                            answer = uiState.answer
//                        )
                    }

                    ChallengeState.COMPLETED -> GameOverScreen(
                        score = uiState.score,
                        totalQuestions = uiState.questions.size
                    )
                }
            }


            if (uiState.showProgress && uiState.progressDuration > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Next question will be available in ")
                    CircularLoading(timeInSeconds = uiState.progressDuration)
                }
            }

            uiState.errorMessage?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                )
            }
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