package org.smp.flagmaster.ui

import org.smp.domain.model.Country
import org.smp.domain.model.DifficultyMode

sealed interface FlagsScreenAction {
    data class OnDigitChange(val index: Int, val value: String) : FlagsScreenAction
    data class OnOptionSelected(val option: Country) : FlagsScreenAction
    data class OnDifficultySelected(val mode: DifficultyMode) : FlagsScreenAction
    data class OnQuestionCountSelected(val count: Int) : FlagsScreenAction
    data object OnSave : FlagsScreenAction
    data object StartQuiz : FlagsScreenAction
    data object OnScheduleChallenge : FlagsScreenAction
    data object ClearError : FlagsScreenAction
    data object PlayAgain : FlagsScreenAction
    data object GoHome : FlagsScreenAction
    data object ShowStats : FlagsScreenAction
    data object HideStats : FlagsScreenAction
    data object SkipFact : FlagsScreenAction
}
