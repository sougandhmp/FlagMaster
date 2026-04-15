package org.smp.flagmaster.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
 *
 * @param flagCountryCode The country code to load the flag image from resources.
 * @param options The list of country options displayed as answer choices.
 * @param onOptionSelected Callback when an option is selected.
 * @param selected The currently selected country (if any).
 * @param result The result status (CORRECT or WRONG) after answering.
 * @param answer The correct answer's ID string (for highlighting the correct option).
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
                    isCorrect = item.id == answer,
                    showResult = result != null,
                ) {
                    onOptionSelected(item)
                }
            }
        }

    }

}

/**
 * Composable displays the flag image for a given country code.
 * Falls back to a white flag emoji if the image is not found.
 *
 * @param countryCode The country code used to load the flag drawable.
 */

@Composable
fun CountryFlag(countryCode: String) {
    val context = LocalContext.current
    val drawableId = remember(countryCode) {
        context.resources.getIdentifier(
            countryCode.lowercase(), "drawable", context.packageName
        )
    }

    if (drawableId != 0) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = "Flag of ${countryCode.uppercase()}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Displays a single option (country) as a button.
 * Applies different colors and feedback text based on the selected and result state.
 *
 * @param country The country represented by this button.
 * @param selected Whether this option was selected by the user.
 * @param onClick Called when the button is clicked.
 * @param answer The correct answer's ID, used to style the correct answer.
 * @param answerResult The result status (CORRECT or WRONG) for the current question.
 */
@Composable
fun OptionButton(
    country: Country,
    selected: Boolean,
    onClick: (Country) -> Unit,
    answer: String? = null,
    answerResult: AnswerResult? = null
) {
    val isCorrect = country.id == answer
    val isWrongSelection = selected && !isCorrect && answerResult != null
    val showCorrect = isCorrect && answerResult != null
    val showWrong = isWrongSelection

    // Background color
    val containerColor = when {
        showCorrect -> MaterialTheme.colorScheme.secondaryContainer
        showWrong -> MaterialTheme.colorScheme.errorContainer
        selected -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    // Border color
    val borderColor = when {
        showCorrect -> MaterialTheme.colorScheme.secondary
        showWrong -> MaterialTheme.colorScheme.error
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { onClick(country) },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, borderColor),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.scrim
            )
        ) {
            Text(text = country.name)
        }

        // Feedback labels
        when {
            showCorrect -> {
                Text(
                    stringResource(R.string.flags_button_correct),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            showWrong -> {
                Text(
                    stringResource(R.string.flags_button_wrong),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            else -> {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val backgroundColor = when {
        showResult && isCorrect -> MaterialTheme.colorScheme.secondaryContainer
        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
        isSelected && !showResult -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        showResult && isCorrect -> MaterialTheme.colorScheme.onSecondaryContainer
        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
        isSelected && !showResult -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderColor = when {
        showResult && isCorrect -> MaterialTheme.colorScheme.secondary
        showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        isSelected && !showResult -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
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
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}


@Composable
@Preview
private fun ChallengePreview() {
    FlagMasterTheme {
        ChallengeView(
            options = listOf(Country("in", "India"), Country("us", "United States")),
            onOptionSelected = {},
            selected = Country("in", "India"),
            result = AnswerResult.CORRECT
        )
    }
}