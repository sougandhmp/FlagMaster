package org.smp.feature.flags.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.smp.domain.model.DifficultyMode
import org.smp.feature.flags.R
import org.smp.feature.flags.FlagsScreenAction
import org.smp.feature.flags.ScheduleTimeUiState
import org.smp.feature.flags.theme.FlagMasterTheme

private val TrophyGold = Color(0xFFFFD700)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChallengeScreen(
    uiState: ScheduleTimeUiState,
    onAction: (FlagsScreenAction) -> Unit,
    userPhotoUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.start_challenge_description),
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(36.dp))

            Text(
                text = "Difficulty",
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                DifficultyMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = uiState.difficultyMode == mode,
                        onClick = { onAction(FlagsScreenAction.OnDifficultySelected(mode)) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index,
                            DifficultyMode.entries.size
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = colorScheme.primaryContainer,
                            activeContentColor = colorScheme.onPrimaryContainer,
                            activeBorderColor = colorScheme.primary,
                            inactiveContainerColor = colorScheme.surfaceContainerHigh,
                            inactiveContentColor = colorScheme.onSurfaceVariant,
                            inactiveBorderColor = colorScheme.outlineVariant,
                        ),
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(mode.label, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${mode.timerMs / 1000}s / question",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.questions),
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant,
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
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = colorScheme.primaryContainer,
                            activeContentColor = colorScheme.onPrimaryContainer,
                            activeBorderColor = colorScheme.primary,
                            inactiveContainerColor = colorScheme.surfaceContainerHigh,
                            inactiveContentColor = colorScheme.onSurfaceVariant,
                            inactiveBorderColor = colorScheme.outlineVariant,
                        ),
                        label = { Text("$count", fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            VibrantCtaButton(
                text = stringResource(R.string.start_now),
                onClick = { onAction(FlagsScreenAction.StartQuiz) },
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onAction(FlagsScreenAction.OnScheduleChallenge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.onBackground),
                border = BorderStroke(1.dp, colorScheme.outline)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.schedule_challenge),
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(
                visible = uiState.showScheduler,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorScheme.surfaceContainerHigh)
                        .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(16.dp))
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
                                tint = colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Set a time",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = colorScheme.outlineVariant
                        )
                        TimerScheduleView(uiState = uiState, onAction = onAction)
                    }
                }
            }
        }

        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = colorScheme.onSurface,
                containerColor = colorScheme.surfaceContainerHigh,
            ),
        ) {
            if (userPhotoUrl != null) {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        IconButton(
            onClick = onLeaderboardClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 12.dp, start = 12.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = colorScheme.onSurface,
                containerColor = colorScheme.surfaceContainerHigh,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Leaderboard",
                modifier = Modifier.size(22.dp),
                tint = TrophyGold
            )
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
