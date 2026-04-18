package org.smp.flagmaster.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.smp.data.sync.FirebaseBackgroundSyncManager
import org.smp.domain.usecase.questions.SeedQuestionsUseCase
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val seedQuestionsUseCase: SeedQuestionsUseCase,
    private val syncManager: FirebaseBackgroundSyncManager,
) : ViewModel() {

    init {
        viewModelScope.launch {
            runCatching {
                syncManager.schedulePeriodic()
                syncManager.scheduleImmediateSync()
            }.onFailure { Timber.e(it, "Failed to schedule sync") }
        }
        viewModelScope.launch {
            runCatching { seedQuestionsUseCase() }
                .onSuccess { Timber.d("Questions seeded") }
                .onFailure { Timber.e(it, "Seeding failed") }
        }
    }

    fun pauseSync() {
        syncManager.pausePeriodicSync()
    }

    fun resumeSync() {
        syncManager.resumePeriodicSync()
    }
}
