package org.smp.flagmaster.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.domain.model.Country
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.AnswerResult
import org.smp.flagmaster.ui.theme.FlagMasterTheme


/**
 * A composable representing the main quiz view.
 * Displays a flag and a grid of country options for the user to choose from.
 */
@Composable
fun ChallengeView(
    flagCountryCode: String = "in",
    options: List<Country>,
    onOptionSelected: (Country) -> Unit,
    selected: Country? = null,
    result: AnswerResult? = null,
    answer: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.guess_the_country_from_the_flag),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CountryFlag(countryCode = flagCountryCode)

        Spacer(Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options) { item ->
                AnswerOption(
                    text = item.name,
                    isSelected = selected == item,
                    isCorrect = item.code == answer,
                    showResult = result != null,
                ) {
                    onOptionSelected(item)
                }
            }
        }
    }
}

/**
 * Displays the flag image for a given country code.
 * Falls back silently if the drawable is not found.
 */
@Composable
fun CountryFlag(countryCode: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/flags/${countryCode.lowercase()}.svg")
            .build(),
        contentDescription = "Flag of ${countryCode.uppercase()}",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

/**
 * Displays a single answer option.
 * - Unselected: white card with subtle border
 * - Selected (pending): primary container fill
 * - Correct (after reveal): solid primary fill, white text, checkmark on right
 * - Wrong selection (after reveal): error container fill, X on right
 */
@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val isWrongSelection = isSelected && !isCorrect

    val containerColor = when {
        showResult && isCorrect        -> MaterialTheme.colorScheme.primary
        showResult && isWrongSelection -> MaterialTheme.colorScheme.errorContainer
        isSelected                     -> MaterialTheme.colorScheme.primaryContainer
        else                           -> Color.White.copy(alpha = 0.85f)
    }

    val textColor = when {
        showResult && isCorrect        -> Color.White
        showResult && isWrongSelection -> MaterialTheme.colorScheme.onErrorContainer
        isSelected                     -> MaterialTheme.colorScheme.onPrimaryContainer
        else                           -> Color(0xFF1A1050)
    }

    val borderColor = when {
        showResult && isCorrect        -> Color.Transparent
        showResult && isWrongSelection -> MaterialTheme.colorScheme.error
        isSelected                     -> MaterialTheme.colorScheme.primary
        else                           -> Color.White.copy(alpha = 0.5f)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "optionScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clickable {
                if (!showResult) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            }
            .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            // Icon badge — slides in on result reveal
            AnimatedVisibility(
                visible = showResult && (isCorrect || isWrongSelection),
                enter = fadeIn() + scaleIn(initialScale = 0.4f),
                exit  = fadeOut() + scaleOut(targetScale = 0.4f),
            ) {
                val icon    = if (isCorrect) Icons.Default.Check else Icons.Default.Close
                val bgColor = if (isCorrect)
                    Color.White.copy(alpha = 0.25f)
                else
                    MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                val iconTint = if (isCorrect)
                    Color.White
                else
                    MaterialTheme.colorScheme.onErrorContainer

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

/**
 * Legacy option button (used by ChallengeView grid — kept for backward compat).
 */
@Composable
fun OptionButton(
    country: Country,
    selected: Boolean,
    onClick: (Country) -> Unit,
    answer: String? = null,
    answerResult: AnswerResult? = null
) {
    val isCorrect = country.code == answer
    val isWrongSelection = selected && !isCorrect && answerResult != null
    val showCorrect = isCorrect && answerResult != null

    val containerColor = when {
        showCorrect      -> MaterialTheme.colorScheme.secondaryContainer
        isWrongSelection -> MaterialTheme.colorScheme.errorContainer
        selected         -> MaterialTheme.colorScheme.primaryContainer
        else             -> Color.Transparent
    }

    val borderColor = when {
        showCorrect      -> MaterialTheme.colorScheme.secondary
        isWrongSelection -> MaterialTheme.colorScheme.error
        selected         -> MaterialTheme.colorScheme.primary
        else             -> MaterialTheme.colorScheme.outline
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { onClick(country) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, borderColor),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.scrim
            )
        ) {
            Text(text = country.name)
        }

        when {
            showCorrect -> Text(
                stringResource(R.string.flags_button_correct),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
            isWrongSelection -> Text(
                stringResource(R.string.flags_button_wrong),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
            else -> Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview
private fun ChallengePreview() {
    FlagMasterTheme {
        ChallengeView(
            options = listOf(Country("India", "in"), Country("United States", "us")),
            onOptionSelected = {},
            selected = Country("India", "in"),
            result = AnswerResult.CORRECT
        )
    }
}
