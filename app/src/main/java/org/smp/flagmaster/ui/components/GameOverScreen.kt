package org.smp.flagmaster.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import org.smp.flagmaster.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun     GameOverScreen(
    score: Int = 0,
    totalQuestions: Int = 15,
    onPlayAgain: () -> Unit = {},
    onViewStats: () -> Unit = {},
    onShare: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val context = LocalContext.current
    val percentage = remember { (score.toFloat() / totalQuestions * 100).toInt() }

    // Animation states
    var startAnimations by remember { mutableStateOf(false) }

    // Card entrance animation
    val cardScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.8f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "cardScale"
    )

    val cardAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600),
        label = "cardAlpha"
    )

    // Score counter animation
    val animatedScore by animateIntAsState(
        targetValue = if (startAnimations) score else 0,
        animationSpec = tween(1500, easing = LinearOutSlowInEasing),
        label = "score"
    )

    // Progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimations) percentage.toFloat() else 0f,
        animationSpec = tween(2000, easing = LinearOutSlowInEasing),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        startAnimations = true
    }

    Box(
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
    ) {
        // Floating flag emojis background
        FloatingFlags()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            GameOverHeader(onBackPressed = onBackPressed)

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Main game over card
                GameOverCard(
                    score = animatedScore,
                    totalQuestions = totalQuestions,
                    percentage = animatedProgress.toInt(),
                    modifier = Modifier
                        .scale(cardScale)
                        .alpha(cardAlpha)
                )

                // Performance message card
                PerformanceMessageCard(
                    percentage = percentage,
                    modifier = Modifier.alpha(cardAlpha)
                )

                // Action buttons
                ActionButtons(
                    onPlayAgain = onPlayAgain,
                    onViewStats = onViewStats,
                    onShare = onShare,
                    onHome = onHome,
                    modifier = Modifier.alpha(cardAlpha)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameOverHeader(
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.flags_challenge),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun GameOverCard(
    score: Int,
    totalQuestions: Int,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Game Over Title
            Text(
                text = stringResource(R.string.game_over),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            // Circular Progress with Percentage
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.size(120.dp),
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "$percentage%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Score Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.score_label),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$score",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/$totalQuestions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceMessageCard(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val message = remember(percentage) {
        when {
            percentage == 100 -> "🎖️ PERFECT! You're a true flag master!"
            percentage >= 80 -> "🏆 Excellent! You really know your flags!"
            percentage >= 60 -> "🎯 Great job! You're becoming a flag expert!"
            percentage >= 40 -> "💪 Keep practicing! You're improving!"
            percentage > 0 -> "🌟 Don't give up! Every expert was once a beginner!"
            else -> "📚 Study more flags and try again! You can do it!"
        }
    }
    val containerColor = when {
        percentage == 100 -> MaterialTheme.colorScheme.tertiary
        percentage >= 80 -> MaterialTheme.colorScheme.secondary
        percentage >= 60 -> MaterialTheme.colorScheme.primary
        percentage >= 40 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val textColor = when {
        percentage == 100 -> MaterialTheme.colorScheme.onTertiary
        percentage >= 80 -> MaterialTheme.colorScheme.onSecondary
        percentage >= 60 -> MaterialTheme.colorScheme.onPrimary
        percentage >= 40 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(20.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ActionButtons(
    onPlayAgain: () -> Unit,
    onViewStats: () -> Unit,
    onShare: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Primary button - Play Again
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.play_again),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Secondary buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onViewStats,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(Color.White.copy(alpha = 0.7f))
                )
            ) {
                Text(text = stringResource(R.string.stats), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = onShare,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(Color.White.copy(alpha = 0.7f))
                )
            ) {
                Text(text = stringResource(R.string.share_action), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = onHome,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(Color.White.copy(alpha = 0.7f))
                )
            ) {
                Text(text = stringResource(R.string.home), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun FloatingFlags() {
    val infiniteTransition = rememberInfiniteTransition(label = "flags")

    val flags = remember { listOf("🇺🇸", "🇬🇧", "🇫🇷", "🇯🇵", "🇩🇪", "🇨🇦", "🇦🇺", "🇮🇳") }

    Box(modifier = Modifier.fillMaxSize()) {
        flags.forEachIndexed { index, flag ->
            val animationDelay = index * 2000
            val duration = 15000 + (index * 1000)

            val yAnimation by infiniteTransition.animateFloat(
                initialValue = 1200f,
                targetValue = -200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "flagY$index"
            )

            val xOffset = (index * 80) % 300
            val rotationAnimation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "flagRotation$index"
            )

            Text(
                text = flag,
                fontSize = 24.sp,
                modifier = Modifier
                    .offset(
                        x = (xOffset + 50).dp,
                        y = yAnimation.dp
                    )
                    .graphicsLayer {
                        rotationZ = rotationAnimation
                        alpha = 0.6f
                    }
            )
        }
    }
}

// Usage in Activity or Navigation
@Composable
fun GameOverRoute(
    score: Int,
    totalQuestions: Int,
    onNavigateBack: () -> Unit,
    onNavigateToGame: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current

    GameOverScreen(
        score = score,
        totalQuestions = totalQuestions,
        onPlayAgain = onNavigateToGame,
        onViewStats = onNavigateToStats,
        onShare = {
            val percentage = (score.toFloat() / totalQuestions * 100).toInt()
            val shareText = "🎯 I scored $score/$totalQuestions ($percentage%) in Flags Challenge! Can you beat my score? 🌍"

            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }

            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share your score"))
        },
        onBackPressed = onNavigateBack,
        onHome = onNavigateToHome
    )
}

// Preview
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun GameOverScreenPreview() {
    MaterialTheme {
        GameOverScreen(
            score = 8,
            totalQuestions = 15
        )
    }
}

// Alternative implementation with custom circular progress
@Composable
fun AnimatedCircularProgressIndicator(
    percentage: Float,
    radius: Float = 50f,
    strokeWidth: Float = 8f,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.tertiary
    Canvas(
        modifier = modifier.size((radius * 2 + strokeWidth).dp)
    ) {
        drawCircularProgress(
            percentage = percentage,
            radius = radius.dp.toPx(),
            strokeWidth = strokeWidth.dp.toPx(),
            trackColor = trackColor,
            progressColor = progressColor,
        )
    }
}

private fun DrawScope.drawCircularProgress(
    percentage: Float,
    radius: Float,
    strokeWidth: Float,
    trackColor: Color,
    progressColor: Color,
) {
    val center = size.width / 2f

    // Background circle
    drawCircle(
        color = trackColor,
        radius = radius,
        center = androidx.compose.ui.geometry.Offset(center, center),
        style = Stroke(strokeWidth)
    )

    // Progress arc
    val sweepAngle = (percentage / 100f) * 360f
    drawArc(
        color = progressColor,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = androidx.compose.ui.geometry.Offset(
            center - radius,
            center - radius
        ),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(strokeWidth, cap = StrokeCap.Round)
    )
}