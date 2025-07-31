package org.smp.flagmaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.flagmaster.R
import org.smp.flagmaster.ui.theme.FlagMasterTheme

/**
 * Displays the final screen after the quiz is completed.
 *
 * Shows a "GAME OVER" label and the user's score formatted as "SCORE : XX/YY".
 *
 * @param score The number of correct answers.
 * @param total The total number of questions in the quiz.
 */
@Composable
fun ChallengeCompleteView(score: Int, total: Int) {
    Column {
        Text(
            stringResource(R.string.game_over),
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        ScoreView(score = score, total = total)
    }

}

/**
 * Displays a "SCORE :" label followed by the user's score.
 *
 * Formats the score side by side using a horizontal [Row].
 *
 * @param score The number of correct answers.
 * @param total The total number of questions in the quiz.
 * @param modifier Optional [Modifier] for styling the container.
 */
@Composable
fun ScoreView(
    score: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    // Row to horizontally align label and score/value
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.score),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        // Score value with underline
        Text(
            text = "$score/$total",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
            )
        )
    }
}


@Composable
@Preview
private fun ScoreViewPreview() {
    FlagMasterTheme {
        ScoreView(score = 7, total = 10)
    }
}

@Composable
@Preview
private fun ChallengeCompleteViewPreview() {
    FlagMasterTheme {
        ChallengeCompleteView(score = 7, total = 10)
    }
}