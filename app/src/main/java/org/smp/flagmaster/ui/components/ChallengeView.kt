package org.smp.flagmaster.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
                OptionButton(
                    item, onClick = { onOptionSelected(item) },
                    selected = selected == item,
                    answer = answer,
                    answerResult = result
                )
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
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    } else {
        Text("🏳️", fontSize = 32.sp)
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
        showCorrect -> Color(0xFFDFF5DC)
        showWrong -> Color(0xFFFFE0E0)
        selected -> Color(0xFFE0E0E0)
        else -> Color.Transparent
    }

    // Border color
    val borderColor = when {
        showCorrect -> Color(0xFF4CAF50)
        showWrong -> Color(0xFFE53935)
        selected -> Color(0xFF2196F3)
        else -> Color.LightGray
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
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            showWrong -> {
                Text(
                    stringResource(R.string.flags_button_wrong),
                    color = Color(0xFFE53935),
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