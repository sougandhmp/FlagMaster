package org.smp.flagmaster.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.domain.model.DifficultyMode
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.FlagsScreenAction
import org.smp.flagmaster.ui.ScheduleTimeUiState
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChallengeScreen(
    uiState: ScheduleTimeUiState,
    onAction: (FlagsScreenAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App icon + title
        Image(
            painter = painterResource(id = R.drawable.ic_world_globe),
            contentDescription = stringResource(R.string.world_globe_description),
            modifier = Modifier
                .sizeIn(maxWidth = 120.dp, maxHeight = 120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.flags_challenge),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.start_challenge_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(36.dp))

        // Difficulty selector
        Text(
            text = "Difficulty",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            DifficultyMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = uiState.difficultyMode == mode,
                    onClick = { onAction(FlagsScreenAction.OnDifficultySelected(mode)) },
                    shape = SegmentedButtonDefaults.itemShape(index, DifficultyMode.entries.size),
                    label = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(mode.label, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${mode.timerMs / 1000}s / question",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Question count selector
        Text(
            text = stringResource(R.string.questions),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        val questionCounts = listOf(5, 10, 15, 20)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            questionCounts.forEachIndexed { index, count ->
                SegmentedButton(
                    selected = uiState.questionCount == count,
                    onClick = { onAction(FlagsScreenAction.OnQuestionCountSelected(count)) },
                    shape = SegmentedButtonDefaults.itemShape(index, questionCounts.size),
                    label = { Text("$count", fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Start Now — primary CTA, full width, prominent
        Button(
            onClick = { onAction(FlagsScreenAction.StartQuiz) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.start_now),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        // Schedule — secondary, outlined, full width
        OutlinedButton(
            onClick = { onAction(FlagsScreenAction.OnScheduleChallenge) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.schedule_challenge),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Scheduler expander
        AnimatedVisibility(
            visible = uiState.showScheduler,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Set a time",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                    TimerScheduleView(uiState = uiState, onAction = onAction)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun StartChallengeScreenPreview() {
    FlagMasterTheme {
        StartChallengeScreen(
            uiState = ScheduleTimeUiState(),
            onAction = {}
        )
    }
}
