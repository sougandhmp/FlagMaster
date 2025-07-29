package org.smp.data.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey val answerId: Int,
    val countryCode: String
)

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val id: Int,     // original ID from JSON
    val countryName: String,
    val answerOwnerId: Int
)
