package org.smp.flagmaster.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlagsChallengeHeader(
    modifier: Modifier = Modifier,
    questionNumber: Int = 1,
    totalQuestions: Int = 15,
    remainingTime: String = "",
    timerTotalSeconds: Int = 30,
    score: Int = 0,
) {
    // ── Timer fraction (for future use / color) ──────────────────────────────
    val timerFraction = remember(remainingTime) {
        val parts = remainingTime.split(":")
        if (parts.size == 2) {
            val mins = parts[0].toIntOrNull() ?: 0
            val secs = parts[1].toIntOrNull() ?: 0
            val total = mins * 60 + secs
            (total.toFloat() / timerTotalSeconds.toFloat()).coerceIn(0f, 1f)
        } else 1f
    }

    val animatedFraction by animateFloatAsState(
        targetValue = timerFraction,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "timerFraction"
    )

    // Display only seconds when < 1 min, otherwise MM:SS
    val timerLabel = remember(remainingTime) {
        val parts = remainingTime.split(":")
        if (parts.size == 2) {
            val mins = parts[0].toIntOrNull() ?: 0
            val secs = parts[1].toIntOrNull() ?: 0
            if (mins > 0) remainingTime else secs.toString()
        } else remainingTime
    }

    // Timer badge color: green → yellow → red as time runs out
    val timerBadgeAlpha = animatedFraction
    val timerContainerColor = MaterialTheme.colorScheme.primary

    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),

        // ── Left: timer badge ────────────────────────────────────────────────
        navigationIcon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(width = 44.dp, height = 36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(timerContainerColor)
            ) {
                Text(
                    text = timerLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp,
                )
            }
        },

        // ── Centre: question counter ─────────────────────────────────────────
        title = {
            Text(
                text = "$questionNumber / $totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },

        // ── Right: score badge ───────────────────────────────────────────────
        actions = {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                    )
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun FlagsChallengeHeaderPreview() {
    FlagsChallengeHeader(
        questionNumber = 15,
        totalQuestions = 15,
        remainingTime = "00:00",
        timerTotalSeconds = 30,
        score = 2,
    )
}
