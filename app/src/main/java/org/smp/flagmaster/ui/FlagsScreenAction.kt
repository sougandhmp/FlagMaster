package org.smp.flagmaster.ui

import org.smp.domain.model.Country
import org.smp.domain.model.DifficultyMode

sealed class FlagsScreenAction {

    class OnDigitChange(val index: Int, val value: String) : FlagsScreenAction()

    class OnOptionSelected(val option: Country) : FlagsScreenAction()

    object OnSave : FlagsScreenAction()

    object StartQuiz : FlagsScreenAction()

    object OnScheduleChallenge : FlagsScreenAction()

    object ClearError : FlagsScreenAction()

    data class OnDifficultySelected(val mode: DifficultyMode) : FlagsScreenAction()

    data class OnQuestionCountSelected(val count: Int) : FlagsScreenAction()

    object PlayAgain : FlagsScreenAction()
    object GoHome : FlagsScreenAction()
    object ShowStats : FlagsScreenAction()
    object HideStats : FlagsScreenAction()
}