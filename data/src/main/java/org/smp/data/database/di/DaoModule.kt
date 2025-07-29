package org.smp.data.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.smp.data.database.FlagsDatabase
import org.smp.data.database.dao.QuestionDao

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun provideQuestionsDao(
        database: FlagsDatabase,
    ): QuestionDao = database.questionDao()


}