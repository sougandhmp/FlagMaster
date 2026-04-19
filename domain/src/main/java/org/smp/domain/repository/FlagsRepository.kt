package org.smp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.smp.domain.model.Question
import org.smp.domain.model.QuizAnswer

interface FlagsRepository {

    /**
     * Seed the local database with questions (fetched from Firebase, falls back to bundled assets).
     * No-op if the database is already seeded.
     */
    suspend fun seedQuestions()

    /**
     * Get all the questions from the database (one-shot).
     * @return List<Question>
     */
    suspend fun getAllQuestions(): List<Question>

    /**
     * Observe questions from the local Room cache.
     * Emits immediately with cached data, then re-emits whenever the cache is refreshed.
     */
    fun observeAllQuestions(): Flow<List<Question>>

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

    /**
     * Schedule a sync with Firebase to get the latest questions.
     */
    fun scheduleSync()
}