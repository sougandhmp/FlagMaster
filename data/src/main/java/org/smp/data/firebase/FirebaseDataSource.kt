package org.smp.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.smp.data.asset_data_source.dto.CountryDto
import org.smp.data.asset_data_source.dto.QuestionDto
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun loadQuestions(): List<QuestionDto> {
        Timber.d("Fetching questions from Firestore…")

        val snapshot = firestore.collection("questions").get().await()

        if (snapshot.isEmpty) {
            Timber.w("Firestore 'questions' collection is empty or missing")
            return emptyList()
        }

        val questions = snapshot.documents.mapNotNull { doc ->
            try {
                @Suppress("UNCHECKED_CAST")
                val countriesList = doc.get("countries") as? List<Map<String, Any>> ?: emptyList()

                QuestionDto(
                    answerId = doc.getString("answer_id") ?: return@mapNotNull null,
                    countryCode = doc.getString("country_code") ?: return@mapNotNull null,
                    fact = doc.getString("fact") ?: "",
                    difficulty = doc.getString("difficulty") ?: "",
                    countries = countriesList.map { map ->
                        CountryDto(
                            countryName = map["country_name"] as? String ?: "",
                            countryCode = map["country_code"] as? String ?: "",
                        )
                    },
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse Firestore document ${doc.id}")
                null
            }
        }

        Timber.d("Fetched ${questions.size} questions from Firestore")
        return questions
    }
}
