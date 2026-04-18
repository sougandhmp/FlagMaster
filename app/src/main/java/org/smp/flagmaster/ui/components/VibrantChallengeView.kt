package org.smp.flagmaster.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.domain.model.Country
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.theme.BlueVibrantTheme
import org.smp.flagmaster.ui.theme.FlagMasterTheme
import org.smp.flagmaster.ui.theme.OrangeVibrantTheme
import org.smp.flagmaster.ui.theme.VibrantThemeConfig

@Composable
fun VibrantChallengeView(
    modifier: Modifier = Modifier,
    config: VibrantThemeConfig = BlueVibrantTheme,
    questionNumber: Int = 15,
    totalQuestions: Int = 15,
    score: Int = 2,
    remainingTime: String = "00:00",
    flagCountryCode: String = "ae",
    options: List<Country>,
    selectedCountry: Country? = null,
    onOptionSelected: (Country) -> Unit = {},
    onSeeResults: () -> Unit = {},
    showResult: Boolean = false,
    correctAnswer: String? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VibrantHeader(
                questionNumber = questionNumber,
                totalQuestions = totalQuestions,
                score = score,
                remainingTime = remainingTime,
                config = config
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Glassmorphic Content Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(config.cardBackground)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.which_country_flag),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        )
                    )

                    // Flag Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CountryFlag(countryCode = flagCountryCode)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Options List
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        options.forEach { country ->
                            VibrantAnswerOption(
                                text = country.name,
                                isSelected = selectedCountry == country,
                                isCorrect = country.code == correctAnswer,
                                showResult = showResult,
                                config = config,
                                onClick = { onOptionSelected(country) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // See Results/Next Question Button
            if (showResult) {
                VibrantCtaButton(
                    text = stringResource(R.string.next_question),
                    config = config,
                    onClick = onSeeResults,
                    modifier = Modifier.alpha(1f)
                )
            } else {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun VibrantHeader(
    questionNumber: Int,
    totalQuestions: Int,
    score: Int,
    remainingTime: String,
    config: VibrantThemeConfig
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Timer Circle (Left)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(config.timerCircleColor)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = remainingTime, // Now expects just seconds, e.g. "18"
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            // Progress Text (Center)
            Text(
                text = "$questionNumber / $totalQuestions",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            // Score Pill (Right)
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(20.dp))
                    .background(config.scorePillColor)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$score",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(config.progressTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(questionNumber.toFloat() / totalQuestions)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(config.progressIndicator)
            )
        }
    }
}

@Composable
fun VibrantAnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    config: VibrantThemeConfig,
    onClick: () -> Unit
) {
    val isWrongSelection = isSelected && !isCorrect

    val background = when {
        showResult && isCorrect -> Color(0xFF4DD0E1).copy(alpha = 0.9f)
        showResult && isWrongSelection -> Color(0xFFE57373).copy(alpha = 0.9f)
        isSelected -> config.optionSelectedBackground
        else -> Color.White.copy(alpha = 0.1f)
    }

    val borderColor = when {
        showResult && (isCorrect || isWrongSelection) -> Color.White.copy(alpha = 0.9f)
        isSelected -> Color.White.copy(alpha = 0.8f)
        else -> Color.White.copy(alpha = 0.2f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp) // Reduced from 68.dp to fit 4 options on screen
            .clip(RoundedCornerShape(16.dp)) // Slightly less rounding for compactness
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !showResult, onClick = onClick)
            .padding(horizontal = 16.dp), // Reduced padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp, // Slightly smaller font
            fontWeight = FontWeight.Medium
        )

        if (isSelected || (showResult && isCorrect)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun VibrantCtaButton(
    text: String,
    config: VibrantThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(Brush.horizontalGradient(config.buttonGradient))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}


@Preview
@Composable
fun BlueVibrantPreview() {
    FlagMasterTheme {
        VibrantChallengeView(
            config = BlueVibrantTheme,
            options = listOf(
                Country("United Arab Emirates", "ae"),
                Country("Macedonia", "mk"),
                Country("Guernsey", "gg")
            ),
            selectedCountry = Country("United Arab Emirates", "ae"),
            showResult = true,
            correctAnswer = "mk"
        )
    }
}

@Preview
@Composable
fun OrangeVibrantPreview() {
    FlagMasterTheme {
        VibrantChallengeView(
            config = OrangeVibrantTheme,
            options = listOf(
                Country("United Arab Emirates", "ae"),
                Country("United Arab Emirates", "ae"),
                Country("Macedonia", "mk"),
                Country("Guernsey", "gg")
            ),
            selectedCountry = Country("United Arab Emirates", "ae")
        )
    }
}
