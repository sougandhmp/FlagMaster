package org.smp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.smp.domain.model.QuizAnswer
import javax.inject.Inject

// Name your Preferences DataStore
private const val DATASTORE_NAME = "flags_Quiz"

// Extension property to create DataStore
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreManager @Inject constructor(private val context: Context) {

    suspend fun saveAnswers(answers: List<QuizAnswer>) {
        val jsonString = Gson().toJson(answers)
        context.dataStore.edit { preferences ->
            preferences[ANSWERS_KEY] = jsonString
        }
    }

    fun observeSavedAnswers(): Flow<List<QuizAnswer>> = context.dataStore.data.map { preferences ->
        val json = preferences[ANSWERS_KEY] ?: return@map emptyList()
        try {
            Gson().fromJson(json, object : TypeToken<List<QuizAnswer>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveChallengeTime(time: Long) {
        context.dataStore.edit { preferences ->
            preferences[CHALLENGE_TIME_KEY] = time
        }
    }

    fun observeChallengeTime(): Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[CHALLENGE_TIME_KEY] ?: 0L
    }

    suspend fun clearQuizAnswersAndTime() {
        context.dataStore.edit { preferences ->
            preferences.remove(ANSWERS_KEY)
            preferences.remove(CHALLENGE_TIME_KEY)
        }
    }


    private val ANSWERS_KEY = stringPreferencesKey("quiz_answers_list")
    private val CHALLENGE_TIME_KEY = longPreferencesKey("challenge_time")
}