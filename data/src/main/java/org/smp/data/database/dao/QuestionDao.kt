package org.smp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.smp.data.database.model.CountryEntity
import org.smp.data.database.model.QuestionEntity
import org.smp.data.database.model.QuestionWithOptions

@Dao
interface QuestionDao {

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Transaction
    @Query("SELECT * FROM questions")
    suspend fun getQuestionsWithOptions(): List<QuestionWithOptions>

    /** Reactive query — emits a new list whenever the questions table changes. */
    @Transaction
    @Query("SELECT * FROM questions")
    fun observeQuestions(): Flow<List<QuestionWithOptions>>

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()

    @Query("DELETE FROM countries")
    suspend fun clearCountries()

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int

    @Transaction
    suspend fun insertSeedData(
        questions: List<QuestionEntity>,
        countries: List<CountryEntity>
    ) {
        insertQuestions(questions)
        insertCountries(countries)
    }

    /**
     * Atomically clears both tables and inserts fresh data.
     * The Room Flow never sees an empty or partial state during a refresh.
     */
    @Transaction
    suspend fun replaceAllData(
        questions: List<QuestionEntity>,
        countries: List<CountryEntity>,
    ) {
        clearQuestions()
        clearCountries()
        insertQuestions(questions)
        insertCountries(countries)
    }
}
