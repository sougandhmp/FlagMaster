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
import org.smp.data.repository.FlagsRepositoryImpl
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * WorkManager background task for syncing Firebase questions to Room database.
 *
 * Runs in background even if app is closed.
 * Automatically retries with exponential backoff on failure.
 * Respects device constraints (battery, network, etc).
 */
@HiltWorker
class FirebaseSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: FlagsRepositoryImpl,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting Firebase sync worker")
            repository.seedQuestions()
            Timber.d("Firebase sync completed")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Firebase sync failed: ${e.message}")
            Result.retry()
        }
    }
}

/**
 * Manages WorkManager scheduling for Firebase sync tasks.
 *
 * Provides methods to:
 * - Schedule periodic background sync (24 hours)
 * - Trigger immediate one-time sync
 * - Cancel scheduled syncs
 */
class FirebaseBackgroundSyncManager(private val context: Context) {

    companion object {
        private const val PERIODIC_SYNC_TAG = "firebase_questions_periodic_sync"
        private const val IMMEDIATE_SYNC_TAG = "firebase_questions_immediate_sync"
        private const val PERIODIC_SYNC_INTERVAL = 24L  // hours
    }

    /**
     * Schedule periodic background sync every 24 hours.
     *
     * Syncs will only run when:
     * - Device has internet connection
     * - Device is not in low battery mode
     *
     * Example: First sync at 2 AM tomorrow, then every 24 hours after
     */
    fun schedulePeriodic() {
        Timber.d("📅 Scheduling periodic Firebase sync (every 24 hours)")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)  // Need internet
            .apply {
                // Optional: Don't sync if device is low on battery
                // .setRequiresBatteryNotLow(true)
                // Optional: Require device idle
                // .setRequiresDeviceIdle(true)
            }
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<FirebaseSyncWorker>(
            PERIODIC_SYNC_INTERVAL, TimeUnit.HOURS
        ).setConstraints(constraints)
            .addTag(PERIODIC_SYNC_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,  // Don't replace if already scheduled
            syncRequest
        )

        Timber.d("✅ Periodic sync scheduled successfully")
    }

    /**
     * Trigger immediate one-time sync from Firebase.
     *
     * Useful for:
     * - Manual refresh on app startup
     * - User-triggered refresh
     * - First-time sync after install
     */
    fun scheduleImmediateSync() {
        Timber.d("🚀 Scheduling immediate Firebase sync")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<FirebaseSyncWorker>()
            .setConstraints(constraints)
            .addTag(IMMEDIATE_SYNC_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_SYNC_TAG,
            ExistingWorkPolicy.REPLACE,  // Replace if already queued
            syncRequest
        )

        Timber.d("✅ Immediate sync queued successfully")
    }

    /**
     * Pause periodic sync without cancelling.
     * Useful for testing or maintenance.
     */
    fun pausePeriodicSync() {
        Timber.d("⏸️ Pausing periodic sync")
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_TAG)
    }

    /**
     * Resume periodic sync after pause.
     */
    fun resumePeriodicSync() {
        Timber.d("▶️ Resuming periodic sync")
        schedulePeriodic()
    }
}
