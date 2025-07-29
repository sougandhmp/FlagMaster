package org.smp.flagmaster.ui.time_schedule

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TimeScheduleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleTimeUiState())
    val uiState = _uiState.asStateFlow()

}