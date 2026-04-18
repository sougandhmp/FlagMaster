package org.smp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.smp.data.database.dao.QuestionDao
import org.smp.data.database.model.AnswerEntity
import org.smp.data.database.model.CountryEntity

@Database(
    entities = [AnswerEntity::class, CountryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FlagsDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE answers ADD COLUMN fact TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
