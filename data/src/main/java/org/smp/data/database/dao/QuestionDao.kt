package org.smp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.smp.data.database.model.AnswerEntity
import org.smp.data.database.model.AnswerWithCountries
import org.smp.data.database.model.CountryEntity

@Dao
interface QuestionDao {

    @Insert
    suspend fun insertQuestion(question: AnswerEntity): Long

    @Insert
    suspend fun insertQuestions(questions: List<AnswerEntity>)

    @Insert
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Transaction
    @Query("SELECT * FROM ANSWERS")
    suspend fun getQuestionsWithOptions(): List<AnswerWithCountries>

    @Query("DELETE FROM ANSWERS")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM ANSWERS")
    suspend fun count(): Int

    @Transaction
    suspend fun insertSeedData(
        questions: List<AnswerEntity>,
        countries: List<CountryEntity>
    ) {
        insertQuestions(questions)
        insertCountries(countries)
    }
}