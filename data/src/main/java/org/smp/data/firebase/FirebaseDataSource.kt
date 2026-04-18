package org.smp.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import org.smp.data.asset_data_source.dto.QuestionDto
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor() {

    private val gson = Gson()
    private val db get() = FirebaseDatabase.getInstance()

    suspend fun loadQuestions(): List<QuestionDto> {
        Timber.d("Fetching questions from RTDB…")
        val snapshot = db.getReference("questions").get().await()
        val value = snapshot.value ?: run {
            Timber.w("RTDB /questions node is null or missing")
            return emptyList()
        }
        val questions = gson.fromJson(gson.toJson(value), Array<QuestionDto>::class.java)
            ?.toList() ?: emptyList()
        Timber.d("Fetched ${questions.size} questions")
        return questions
    }
}
