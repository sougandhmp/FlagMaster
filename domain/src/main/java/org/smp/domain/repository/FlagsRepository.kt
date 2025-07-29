package org.smp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer

interface FlagsRepository {

    /**
     * Seed the database with questions from the assets
     * If the database is already seeded, this method will not do anything
     */
    suspend fun seedQuestionsFromAsset()

    /**
     * Get all the questions from the database
     * @return List<Question>
     */
    suspend fun getAllQuestions(): List<Question>

    /**
     * Save the quiz answers to the datastore
     *
     * @param answers List<QuizAnswer>
     */
    suspend fun saveQuizAnswers(answers: List<QuizAnswer>)


    /**
     * Save the challenge time to the datastore
     *
     * @param time Long
     */
    suspend fun saveChallengeTime(time: Long)


    /**
     * Observe the quiz answers from the datastore
     *
     * @return Flow<List<QuizAnswer>>
     */
    fun observeQuizAnswers(): Flow<List<QuizAnswer>>

    /**
     * Observe the challenge time from the datastore
     *
     * @return Flow<Long>
     */
    fun observeChallengeTime(): Flow<Long>

    /**
     * Clear the quiz answers and challenge time from the datastore
     *
     * @return Unit
     */
    suspend fun clearQuizAnswersAndTime()
}