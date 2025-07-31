package org.smp.flagmaster.ui

import org.smp.domain.model.Country

sealed class FlagsScreenAction {

    class OnDigitChange(val index: Int, val value: String) : FlagsScreenAction()

    class OnOptionSelected(val option: Country) : FlagsScreenAction()

    object OnSave : FlagsScreenAction()
}