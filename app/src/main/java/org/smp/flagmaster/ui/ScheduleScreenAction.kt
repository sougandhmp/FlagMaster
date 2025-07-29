package org.smp.flagmaster.ui

import org.smp.domain.model.Country

sealed class ScheduleScreenAction {

    class OnDigitChange(val index: Int, val value: String) : ScheduleScreenAction()

    class OnOptionSelected(val option: Country) : ScheduleScreenAction()

    object OnSave : ScheduleScreenAction()
}