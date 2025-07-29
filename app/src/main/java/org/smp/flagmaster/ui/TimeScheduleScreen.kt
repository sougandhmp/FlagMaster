package org.smp.flagmaster.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.smp.flagmaster.ui.components.ChallengeCompleteView
import org.smp.flagmaster.ui.components.ChallengeScheduledView
import org.smp.flagmaster.ui.components.ChallengeView
import org.smp.flagmaster.ui.components.CircularLoading
import org.smp.flagmaster.ui.components.CountDownView
import org.smp.flagmaster.ui.components.FlagsChallengeHeader
import org.smp.flagmaster.ui.components.TimerScheduleView
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@Composable
fun TimeScheduleRoute(
    viewModel: TimeScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TimeScheduleScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScheduleScreen(
    uiState: ScheduleTimeUiState,
    onAction: (ScheduleScreenAction) -> Unit = {},
) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FlagsChallengeHeader(
                    time = if (uiState.challengeState == ChallengeState.IN_PROGRESS) uiState.remainingTime else null,
                    pageCount = if (uiState.challengeState == ChallengeState.IN_PROGRESS) "${uiState.questionIndex + 1}/${uiState.questions.size}" else null
                )
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 8.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )

                when (uiState.challengeState) {
                    ChallengeState.NOT_SCHEDULED -> TimerScheduleView(uiState, onAction)

                    ChallengeState.SCHEDULED ->
                        ChallengeScheduledView(scheduledTime = uiState.scheduledTime)

                    ChallengeState.COUNT_DOWN ->
                        CountDownView(remainingTime = uiState.remainingTime)

                    ChallengeState.IN_PROGRESS ->
                        ChallengeView(
                            flagCountryCode = uiState.currentQuestion?.countryCode ?: "in",
                            options = uiState.currentQuestion?.options.orEmpty(),
                            onOptionSelected = {
                                onAction(ScheduleScreenAction.OnOptionSelected(it))
                            },
                            selected = uiState.selectedOption,
                            result = uiState.answerResult,
                            answer = uiState.answer
                        )

                    ChallengeState.COMPLETED -> ChallengeCompleteView(
                        score = uiState.score,
                        total = uiState.questions.size
                    )
                }
            }


            if (uiState.showProgress && uiState.progressDuration > 0) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
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
        TimeScheduleScreen(
            uiState = ScheduleTimeUiState(),
            onAction = {}
        )
    }
}