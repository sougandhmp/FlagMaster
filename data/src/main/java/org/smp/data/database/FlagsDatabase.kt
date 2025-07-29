package org.smp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.smp.data.database.dao.QuestionDao
import org.smp.data.database.model.AnswerEntity
import org.smp.data.database.model.CountryEntity

@Database(
    entities = [AnswerEntity::class, CountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlagsDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}