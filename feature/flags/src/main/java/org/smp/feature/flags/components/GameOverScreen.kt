package org.smp.feature.flags.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.core.ui.ConfettiShower
import org.smp.feature.flags.R
import org.smp.feature.flags.theme.FlagMasterTheme

@Composable
fun GameOverScreen(
    score: Int = 2,
    totalQuestions: Int = 10,
    onPlayAgain: () -> Unit = {},
    onViewStats: () -> Unit = {},
    onShare: () -> Unit = {},
    onHome: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    val nearMiss = percentage in 85..89 || percentage in 75..79 || percentage in 55..59

    val grade = when {
        percentage >= 90 -> "S"
        percentage >= 80 -> "A"
        percentage >= 60 -> "B"
        percentage >= 40 -> "C"
        else -> "F"
    }
    val gradeColor = when {
        percentage >= 80 -> colorScheme.tertiary
        percentage >= 40 -> colorScheme.secondary
        else -> colorScheme.error
    }
    val isTopGrade = percentage >= 80

    if (isTopGrade) {
        ConfettiShower(
            colors = listOf(colorScheme.tertiary, colorScheme.primary, colorScheme.secondary)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onBackground
                )
            }
            Text(
                text = "Flags Challenge",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.game_over).uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onBackground,
                    letterSpacing = 1.sp,
                )
            )

            Text(
                text = "You scored $score out of $totalQuestions",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            if (nearMiss) {
                Text(
                    text = "So close! Just one more for the next grade! 🎯",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.secondary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Score card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(colorScheme.surfaceContainerHigh)
                    .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(32.dp))
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                    GradeCircularProgress(
                        percentage = percentage / 100f,
                        color = gradeColor
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = grade,
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                            fontWeight = FontWeight.Black,
                            color = gradeColor
                        )
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tip box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorScheme.surfaceContainerHigh)
                    .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Don't give up — every expert\nwas once a beginner!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_scale"
            )

            VibrantCtaButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                modifier = Modifier.graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomActionItem(
                    icon = Icons.Default.InsertChart,
                    label = "Stats",
                    onClick = onViewStats
                )
                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = colorScheme.outlineVariant
                )
                BottomActionItem(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = onShare
                )
                VerticalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = colorScheme.outlineVariant
                )
                BottomActionItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    onClick = onHome
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BottomActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun GradeCircularProgress(percentage: Float, color: Color) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    var animPlayed by remember { mutableStateOf(false) }
    val curPercentage by animateFloatAsState(
        targetValue = if (animPlayed) percentage else 0f,
        animationSpec = tween(1000),
        label = "percentageAnimation"
    )
    LaunchedEffect(true) { animPlayed = true }

    Canvas(modifier = Modifier.size(180.dp)) {
        drawCircle(
            color = trackColor,
            style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = color.copy(alpha = 0.15f),
            startAngle = -90f,
            sweepAngle = 360 * curPercentage,
            useCenter = false,
            style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        drawArc(
            brush = Brush.sweepGradient(listOf(color.copy(alpha = 0.6f), color)),
            startAngle = -90f,
            sweepAngle = 360 * curPercentage,
            useCenter = false,
            style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameOverScreenPreview() {
    FlagMasterTheme {
        GameOverScreen(score = 2, totalQuestions = 10)
    }
}
