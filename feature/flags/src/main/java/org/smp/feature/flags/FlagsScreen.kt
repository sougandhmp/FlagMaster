package org.smp.feature.flags

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.smp.core.ui.GradientBlobBackground
import org.smp.feature.flags.components.ChallengeScheduledView
import org.smp.feature.flags.components.CountDownView
import org.smp.feature.flags.components.GameOverScreen
import org.smp.feature.flags.components.QuestionScreen
import org.smp.feature.flags.components.StartChallengeScreen
import org.smp.feature.flags.components.StatsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlagsChallengeScreen(
    uiState: ScheduleTimeUiState,
    onAction: (FlagsScreenAction) -> Unit = {},
    userPhotoUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onAction(FlagsScreenAction.ClearError)
        }
    }

    // Background is painted full-bleed here, behind the system bars, before any
    // inset padding is applied — matching the pattern used on the login screen.
    val showGradientBackground = uiState.challengeState == ChallengeState.NOT_SCHEDULED ||
            uiState.challengeState == ChallengeState.COMPLETED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        if (showGradientBackground) {
            GradientBlobBackground(
                colors = listOf(colorScheme.primary, colorScheme.tertiary, colorScheme.secondary),
            ) {}
        }

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
            }
        ) { innerPadding ->
        AnimatedContent(
            targetState = uiState.challengeState,
            transitionSpec = {
                when (targetState) {
                    ChallengeState.COUNT_DOWN ->
                        scaleIn(
                            initialScale = 0.85f,
                            animationSpec = tween(350)
                        ) + fadeIn(tween(350)) togetherWith
                                scaleOut(
                                    targetScale = 1.05f,
                                    animationSpec = tween(200)
                                ) + fadeOut(tween(200))

                    ChallengeState.IN_PROGRESS ->
                        slideInHorizontally(tween(350)) { it / 2 } + fadeIn(tween(350)) togetherWith
                                slideOutHorizontally(tween(200)) { -it / 2 } + fadeOut(tween(200))

                    ChallengeState.COMPLETED ->
                        slideInVertically(tween(400)) { it / 3 } + fadeIn(tween(400)) togetherWith
                                slideOutVertically(tween(250)) { -it / 3 } + fadeOut(tween(250))

                    else ->
                        fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 6 } togetherWith
                                fadeOut(tween(200))
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
            label = "challengeState"
        ) { state ->
            when (state) {
                ChallengeState.NOT_SCHEDULED -> StartChallengeScreen(
                    uiState = uiState,
                    onAction = onAction,
                    userPhotoUrl = userPhotoUrl,
                    onProfileClick = onProfileClick,
                    onLeaderboardClick = onLeaderboardClick,
                )

                ChallengeState.SCHEDULED ->
                    ChallengeScheduledView(scheduledTime = uiState.scheduledTime)

                ChallengeState.COUNT_DOWN ->
                    CountDownView(remainingTime = uiState.remainingTime)

                ChallengeState.IN_PROGRESS -> uiState.currentQuestion?.let { currentQuestion ->
                    QuestionScreen(
                        modifier = Modifier.fillMaxSize(),
                        question = currentQuestion,
                        questionNumber = uiState.questionIndex + 1,
                        totalQuestions = uiState.questions.size.coerceAtLeast(1),
                        score = uiState.score,
                        remainingTime = uiState.remainingTime,
                        selectedAnswer = uiState.selectedOption?.code,
                        showResult = uiState.answerResult != null,
                        onAnswerSelected = { onAction(FlagsScreenAction.OnOptionSelected(it)) },
                        onNextQuestion = { onAction(FlagsScreenAction.SkipFact) },
                        streak = uiState.currentStreak,
                        factCountdown = uiState.factCountdown,
                    )
                }

                ChallengeState.COMPLETED -> {
                    val context = LocalContext.current
                    val total = uiState.questions.size.coerceAtLeast(1)
                    if (uiState.showStats) {
                        StatsScreen(
                            questions = uiState.questions,
                            answers = uiState.answers,
                            score = uiState.score,
                            onBack = { onAction(FlagsScreenAction.HideStats) }
                        )
                    } else {
                        GameOverScreen(
                            score = uiState.score,
                            totalQuestions = total,
                            onPlayAgain = { onAction(FlagsScreenAction.PlayAgain) },
                            onViewStats = { onAction(FlagsScreenAction.ShowStats) },
                            onShare = {
                                val pct = (uiState.score.toFloat() / total * 100).toInt()
                                val text =
                                    "I scored ${uiState.score}/$total ($pct%) in Flags Challenge! Can you beat me? 🌍"
                                val intent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                context.startActivity(
                                    android.content.Intent.createChooser(
                                        intent,
                                        "Share your score"
                                    )
                                )
                            },
                            onHome = { onAction(FlagsScreenAction.GoHome) },
                            onBackPressed = { onAction(FlagsScreenAction.GoHome) },
                        )
                    }
                }
            }
        }
        }
    }
}
