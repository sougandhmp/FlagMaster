package org.smp.flagmaster.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.flagmaster.R
import org.smp.core.ui.PurpleVibrantTheme
import org.smp.core.ui.VibrantBackground
import org.smp.core.ui.VibrantThemeConfig
import org.smp.flagmaster.ui.theme.FlagMasterTheme

@Composable
fun GameOverScreen(
    score: Int = 2,
    totalQuestions: Int = 10,
    config: VibrantThemeConfig = PurpleVibrantTheme,
    onPlayAgain: () -> Unit = {},
    onViewStats: () -> Unit = {},
    onShare: () -> Unit = {},
    onHome: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    val grade = when {
        percentage >= 90 -> "S"
        percentage >= 80 -> "A"
        percentage >= 60 -> "B"
        percentage >= 40 -> "C"
        else -> "F"
    }
    val gradeColor = when {
        percentage >= 80 -> Color(0xFF69F0AE)
        percentage >= 40 -> Color(0xFFFFD54F)
        else -> Color(0xFFFF7043)
    }

    VibrantBackground(config = config) {
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
                        tint = Color.White
                    )
                }
                Text(
                    text = "Flags Challenge",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
                        color = Color.White,
                        letterSpacing = 1.sp,
                        fontSize = 32.sp
                    )
                )

                Text(
                    text = "You scored $score out of $totalQuestions",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Score card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(config.cardBackground)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
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
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                                color = gradeColor
                            )
                            Text(
                                text = "$percentage%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
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
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Don't give up — every expert\nwas once a beginner!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                VibrantCtaButton(
                    text = stringResource(R.string.play_again),
                    config = config,
                    onClick = onPlayAgain,
                    modifier = Modifier
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
                        color = Color.White.copy(alpha = 0.2f)
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
                        color = Color.White.copy(alpha = 0.2f)
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
}

@Composable
fun BottomActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
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
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
fun GradeCircularProgress(percentage: Float, color: Color) {
    var animPlayed by remember { mutableStateOf(false) }
    val curPercentage by animateFloatAsState(
        targetValue = if (animPlayed) percentage else 0f,
        animationSpec = tween(1000),
        label = "percentageAnimation"
    )
    LaunchedEffect(true) { animPlayed = true }

    Canvas(modifier = Modifier.size(180.dp)) {
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
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

@Preview(showBackground = true, backgroundColor = 0xFF001F26)
@Composable
private fun GameOverScreenPreview() {
    FlagMasterTheme {
        GameOverScreen(score = 2, totalQuestions = 10)
    }
}
