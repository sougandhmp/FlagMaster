package org.smp.feature.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.smp.domain.model.UserStats
import org.smp.domain.usecase.leaderboard.ObserveTopPlayersUseCase
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    observeTopPlayersUseCase: ObserveTopPlayersUseCase
) : ViewModel() {

    val topPlayers: StateFlow<List<UserStats>> = observeTopPlayersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
