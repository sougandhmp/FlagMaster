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
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer
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
) : FlagsRepository {

    /**
     * Offline-first seeding strategy with network awareness:
     * 1. Check network availability before attempting Firebase
     * 2. Try to fetch fresh questions from Firebase and replace the Room cache.
     * 3. If Firebase is unreachable (offline / not configured):
     *    - If Room already has data → silently keep it (offline-first).
     *    - If Room is empty (first launch, no network) → fall back to bundled assets.
     */
    override suspend fun seedQuestions() {
        withContext(ioDispatcher) {
            val hasCache = questionDao.count() > 0

            try {
                if (!networkStateManager.isNetworkAvailable()) {
                    Timber.d("No network available - using cached data")
                    return@withContext
                }

                val questions = firebaseDataSource.loadQuestions()

                if (questions.isEmpty()) {
                    Timber.w("Firebase returned empty questions")
                    return@withContext
                }

                val questionsEntity = questions.map { it.toQuestionEntity() }
                val countryOptionEntities = questions.flatMap { it.toCountryEntities() }
                Timber.d("Firebase sync: ${questions.size} questions, ${countryOptionEntities.size} options")

                questionDao.replaceAllData(questionsEntity, countryOptionEntities)

                Timber.d("Room cache updated successfully")

            } catch (e: Exception) {
                when {
                    hasCache -> {
                        // Firebase failed but we have cached data
                        Timber.w(e, "Firebase unreachable - serving ${questionDao.count()} cached questions")
                    }
                    else -> {
                        // No cache and Firebase failed - fall back to assets
                        Timber.w(e, "Firebase failed and no cache - falling back to bundled assets")
                        val questions = assetDataSource.loadCountriesFromAssets()
                        questionDao.insertSeedData(
                            questions.map { it.toQuestionEntity() },
                            questions.flatMap { it.toCountryEntities() }
                        )
                        Timber.d("Seeded from assets: ${questions.size} questions")
                    }
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
}
