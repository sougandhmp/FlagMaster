package org.smp.flagmaster.ui

import android.app.Activity
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.smp.flagmaster.ui.components.ChallengeScheduledView
import org.smp.flagmaster.ui.components.CountDownView
import org.smp.flagmaster.ui.components.GameOverScreen
import org.smp.flagmaster.ui.components.QuestionScreen
import org.smp.flagmaster.ui.components.StartChallengeScreen
import org.smp.flagmaster.ui.components.StatsScreen
import org.smp.flagmaster.ui.components.VibrantBackground
import org.smp.flagmaster.ui.theme.BlueVibrantTheme
import org.smp.flagmaster.ui.theme.FlagMasterTheme
import org.smp.flagmaster.ui.theme.OrangeVibrantTheme
import org.smp.flagmaster.ui.theme.RoseVibrantTheme
import org.smp.flagmaster.ui.theme.TealVibrantTheme
import org.smp.flagmaster.ui.theme.allVibrantThemes

@Composable
fun FlagsChallengeRoute(onProfileClick: () -> Unit = {}) {
    val viewModel: FlagsChallengeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FlagsChallengeScreen(uiState = uiState, onAction = viewModel::onAction, onProfileClick = onProfileClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlagsChallengeScreen(
    uiState: ScheduleTimeUiState,
    onAction: (FlagsScreenAction) -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onAction(FlagsScreenAction.ClearError)
        }
    }

    val theme = when (uiState.challengeState) {
        ChallengeState.NOT_SCHEDULED -> TealVibrantTheme
        ChallengeState.SCHEDULED -> BlueVibrantTheme
        ChallengeState.COUNT_DOWN -> OrangeVibrantTheme
        ChallengeState.IN_PROGRESS -> allVibrantThemes[uiState.questionIndex % allVibrantThemes.size]

        ChallengeState.COMPLETED -> RoseVibrantTheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowInsetsControllerCompat(window, view).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }

    VibrantBackground(config = theme) {
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
                    .padding(innerPadding),
                label = "challengeState"
            ) { state ->
                when (state) {
                    ChallengeState.NOT_SCHEDULED -> StartChallengeScreen(
                        uiState = uiState,
                        onAction = onAction,
                        config = theme,
                        onProfileClick = onProfileClick,
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
                            config = theme,
                            streak = uiState.currentStreak,
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
                                config = theme,
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
