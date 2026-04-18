package org.smp.flagmaster.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    questions: List<Question>,
    answers: List<QuizAnswer>,
    score: Int,
    onBack: () -> Unit,
) {
    val answerMap = answers.associateBy { it.questionId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Results",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                SummaryBanner(score = score, total = questions.size)
                Spacer(Modifier.height(4.dp))
            }

            itemsIndexed(questions) { index, question ->
                val answer = answerMap[question.questionId]
                val correctOption = question.options.firstOrNull { it.code == question.answerId }
                val selectedOption =
                    question.options.firstOrNull { it.code == answer?.selectedOption }
                val isCorrect = answer?.isCorrect == true
                val wasAnswered = answer != null && answer.selectedOption.isNotEmpty()

                QuestionResultCard(
                    index = index + 1,
                    question = question,
                    correctName = correctOption?.name ?: question.answerId,
                    selectedName = if (wasAnswered) selectedOption?.name
                        ?: answer.selectedOption else null,
                    isCorrect = isCorrect,
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SummaryBanner(score: Int, total: Int) {
    val percentage = if (total > 0) (score * 100) / total else 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Final Score",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "$score / $total",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QuestionResultCard(
    index: Int,
    question: Question,
    correctName: String,
    selectedName: String?,
    isCorrect: Boolean,
) {
    val cardColor = when {
        isCorrect -> MaterialTheme.colorScheme.secondaryContainer
        selectedName == null -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val indicatorColor = when {
        isCorrect -> MaterialTheme.colorScheme.secondary
        selectedName == null -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag thumbnail
            Surface(
                modifier = Modifier
                    .size(60.dp, 40.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CountryFlag(countryCode = question.countryCode)
                }
            }

            Spacer(Modifier.width(12.dp))

            // Question details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Q$index  ·  $correctName",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (isCorrect) MaterialTheme.colorScheme.onSecondaryContainer
                    else if (selectedName == null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onErrorContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                val answerLabel = when {
                    isCorrect -> "Correct"
                    selectedName == null -> "No answer"
                    else -> "You answered: $selectedName"
                }
                Text(
                    text = answerLabel,
                    fontSize = 12.sp,
                    color = indicatorColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // Result icon
            when {
                isCorrect -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )

                selectedName != null -> Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )

                else -> Text(
                    text = "—",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 18.sp
                )
            }
        }
    }
}
