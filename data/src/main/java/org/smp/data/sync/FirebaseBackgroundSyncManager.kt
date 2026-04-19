package org.smp.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.smp.domain.repository.FlagsRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class FirebaseSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: FlagsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.seedQuestions()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Firebase sync failed")
            Result.retry()
        }
    }
}

class FirebaseBackgroundSyncManager(private val context: Context) {

    companion object {
        private const val PERIODIC_SYNC_TAG = "firebase_questions_periodic_sync"
        private const val IMMEDIATE_SYNC_TAG = "firebase_questions_immediate_sync"
        private const val PERIODIC_SYNC_INTERVAL = 24L
    }

    fun schedulePeriodic() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = PeriodicWorkRequestBuilder<FirebaseSyncWorker>(PERIODIC_SYNC_INTERVAL, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag(PERIODIC_SYNC_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun scheduleImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<FirebaseSyncWorker>()
            .setConstraints(constraints)
            .addTag(IMMEDIATE_SYNC_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_SYNC_TAG,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun pausePeriodicSync() {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_TAG)
    }

    fun resumePeriodicSync() {
        schedulePeriodic()
    }
}
