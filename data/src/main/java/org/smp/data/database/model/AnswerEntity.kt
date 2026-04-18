package org.smp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Stores each quiz question. Table name "questions" (renamed from "answers" in migration 2→3). */
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val questionId: Int = 0,
    val answerId: String,
    val countryCode: String,
    val fact: String = "",
    val difficulty: String = "",
)

/** Stores the answer options for a question (one row per option, 4 per question). */
@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val code: String,
    val countryName: String,
    @ColumnInfo(name = "questionId") val questionId: String,
)

