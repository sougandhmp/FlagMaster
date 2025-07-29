package org.smp.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.smp.data.asset_data_source.AssetDataSource
import org.smp.data.asset_data_source.dto.toCountryEntities
import org.smp.data.asset_data_source.dto.toQuestionEntity
import org.smp.data.database.dao.QuestionDao
import org.smp.data.database.di.IoDispatcher
import org.smp.data.database.model.toQuestion
import org.smp.data.datastore.DataStoreManager
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer
import org.smp.domain.repository.FlagsRepository
import javax.inject.Inject

class FlagsRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val assetDataSource: AssetDataSource,
    private val dataStoreManager: DataStoreManager,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FlagsRepository {
    override suspend fun seedQuestionsFromAsset() {
        withContext(ioDispatcher) {
            if (questionDao.count() == 0) {
                val questions = assetDataSource.loadCountriesFromAssets()
                val questionEntities = questions.map { question -> question.toQuestionEntity() }
                val countries = questions.flatMap { it.toCountryEntities() }
                questionDao.insertSeedData(questionEntities, countries)
            }
        }
    }

    override suspend fun getAllQuestions(): List<Question> = withContext(ioDispatcher) {
        questionDao.getQuestionsWithOptions().map { it.toQuestion() }
    }

    override suspend fun saveQuizAnswers(answers: List<QuizAnswer>) {
        withContext(ioDispatcher) {
            dataStoreManager.saveAnswers(answers)
        }
    }

    override fun observeChallengeTime(): Flow<Long> = dataStoreManager.observeChallengeTime()


    override suspend fun saveChallengeTime(time: Long) {
        withContext(ioDispatcher) {
            dataStoreManager.saveChallengeTime(time)
        }
    }

    override fun observeQuizAnswers(): Flow<List<QuizAnswer>> =
        dataStoreManager.observeSavedAnswers()

    override suspend fun clearQuizAnswersAndTime() {
        withContext(ioDispatcher) {
            dataStoreManager.clearQuizAnswersAndTime()
        }
    }
}




