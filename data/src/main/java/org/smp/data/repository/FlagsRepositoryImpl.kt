package org.smp.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.smp.data.asset_data_source.AssetDataSource
import org.smp.data.asset_data_source.dto.toCountryEntities
import org.smp.data.asset_data_source.dto.toQuestionEntity
import org.smp.data.database.dao.QuestionDao
import org.smp.data.database.di.IoDispatcher
import org.smp.data.database.model.toQuestion
import org.smp.data.datastore.DataStoreManager
import org.smp.data.firebase.FirebaseDataSource
import org.smp.data.sync.NetworkStateManager
import org.smp.data.sync.FirebaseBackgroundSyncManager
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer
import org.smp.domain.repository.AuthRepository
import org.smp.domain.repository.FlagsRepository
import timber.log.Timber
import javax.inject.Inject

class FlagsRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val assetDataSource: AssetDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val dataStoreManager: DataStoreManager,
    private val networkStateManager: NetworkStateManager,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val syncManager: FirebaseBackgroundSyncManager,
    private val authRepository: AuthRepository,
) : FlagsRepository {

    /**
     * Offline-first seeding strategy with network awareness:
     * 1. Try to fetch fresh questions from Firebase if network is available.
     * 2. If Firebase succeeds → replace Room cache with fresh data.
     * 3. If Firebase fails OR is unreachable:
     *    - If Room already has data → silently keep it (offline-first).
     *    - If Room is empty (first launch) → fall back to bundled assets.
     */
    override suspend fun seedQuestions() {
        withContext(ioDispatcher) {
            val hasCache = questionDao.count() > 0

            // Try Firebase first if network is available and user is authenticated
            if (networkStateManager.isNetworkAvailable()) {
                if (authRepository.getCurrentUser() == null) {
                    Timber.d("Skipping Firebase sync: User not authenticated")
                } else {
                    try {
                        val questions = firebaseDataSource.loadQuestions()

                        if (questions.isNotEmpty()) {
                            val questionsEntity = questions.map { it.toQuestionEntity() }
                            val countryOptionEntities = questions.flatMap { it.toCountryEntities() }
                            Timber.d("Firebase sync: ${questions.size} questions, ${countryOptionEntities.size} options")

                            questionDao.replaceAllData(questionsEntity, countryOptionEntities)
                            Timber.d("Room cache updated successfully from Firebase")
                            return@withContext
                        } else {
                            Timber.w("Firebase returned empty questions")
                        }
                    } catch (e: Exception) {
                        Timber.w(e, "Firebase sync failed, will use cache or assets")
                    }
                }
            } else {
                Timber.d("No network available")
            }

            // Fallback strategy: use cache if available, otherwise seed from assets
            if (hasCache) {
                Timber.d("Using existing ${questionDao.count()} cached questions")
            } else {
                // First launch with no network or Firebase failed - seed from bundled assets
                Timber.i("No cache found - seeding from bundled assets")
                try {
                    val questions = assetDataSource.loadCountriesFromAssets()
                    questionDao.insertSeedData(
                        questions.map { it.toQuestionEntity() },
                        questions.flatMap { it.toCountryEntities() }
                    )
                    Timber.d("Seeded from assets: ${questions.size} questions")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to seed from assets - database will remain empty")
                }
            }
        }
    }

    /** One-shot read from Room (kept for backward compatibility). */
    override suspend fun getAllQuestions(): List<Question> = withContext(ioDispatcher) {
        questionDao.getQuestionsWithOptions().map { it.toQuestion() }
    }

    /**
     * Reactive Room Flow — emits immediately with cached data, then re-emits
     * automatically whenever [seedQuestions] writes fresh data to the cache.
     */
    override fun observeAllQuestions(): Flow<List<Question>> =
        questionDao.observeQuestions().map { list -> list.map { it.toQuestion() } }

    override suspend fun saveQuizAnswers(answers: List<QuizAnswer>) {
        withContext(ioDispatcher) { dataStoreManager.saveAnswers(answers) }
    }

    override fun observeChallengeTime(): Flow<Long> = dataStoreManager.observeChallengeTime()

    override suspend fun saveChallengeTime(time: Long) {
        withContext(ioDispatcher) { dataStoreManager.saveChallengeTime(time) }
    }

    override fun observeQuizAnswers(): Flow<List<QuizAnswer>> =
        dataStoreManager.observeSavedAnswers()

    override suspend fun clearQuizAnswersAndTime() {
        withContext(ioDispatcher) { dataStoreManager.clearQuizAnswersAndTime() }
    }

    override fun scheduleSync() {
        syncManager.scheduleImmediateSync()
    }
}
