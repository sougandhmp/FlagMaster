package org.smp.flagmaster.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverScreen(
    score: Int = 0,
    totalQuestions: Int = 15,
    onPlayAgain: () -> Unit = {},
    onViewStats: () -> Unit = {},
    onShare: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val percentage = remember { (score.toFloat() / totalQuestions * 100).toInt() }

    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { startAnimations = true }

    val cardScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.9f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "cardScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(500),
        label = "cardAlpha"
    )
    val animatedScore by animateIntAsState(
        targetValue = if (startAnimations) score else 0,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "score"
    )
    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimations) percentage / 100f else 0f,
        animationSpec = tween(1500, easing = LinearOutSlowInEasing),
        label = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.flags_challenge),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(cardScale)
                    .alpha(cardAlpha),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.game_over),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    val grade = when {
                        percentage == 100 -> "S"
                        percentage >= 80 -> "A"
                        percentage >= 60 -> "B"
                        percentage >= 40 -> "C"
                        else -> "F"
                    }
                    val gradeColor = when (grade) {
                        "S", "A" -> MaterialTheme.colorScheme.secondary
                        "B", "C" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(130.dp),
                            strokeWidth = 10.dp,
                            color = gradeColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = grade,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = gradeColor
                            )
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.score_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$animatedScore",
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "/$totalQuestions",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }
                }
            }

            // Performance message
            PerformanceMessageCard(
                percentage = percentage,
                modifier = Modifier.alpha(cardAlpha)
            )

            // Action buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = stringResource(R.string.play_again),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewStats,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(text = stringResource(R.string.stats), fontWeight = FontWeight.Medium)
                    }
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(text = stringResource(R.string.share_action), fontWeight = FontWeight.Medium)
                    }
                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(text = stringResource(R.string.home), fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PerformanceMessageCard(percentage: Int, modifier: Modifier = Modifier) {
    val message = when {
        percentage == 100 -> "Perfect! You're a true flag master!"
        percentage >= 80 -> "Excellent! You really know your flags!"
        percentage >= 60 -> "Great job! You're becoming a flag expert!"
        percentage >= 40 -> "Keep practising! You're improving!"
        percentage > 0 -> "Don't give up! Every expert was once a beginner!"
        else -> "Study more flags and try again!"
    }
    val containerColor = when {
        percentage >= 80 -> MaterialTheme.colorScheme.secondaryContainer
        percentage >= 40 -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val textColor = when {
        percentage >= 80 -> MaterialTheme.colorScheme.onSecondaryContainer
        percentage >= 40 -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameOverScreenPreview() {
    FlagMasterTheme {
        GameOverScreen(score = 11, totalQuestions = 15)
    }
}
