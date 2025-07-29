package org.smp.data.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.smp.data.database.FlagsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesFlagsDatabase(
        @ApplicationContext context: Context,
    ): FlagsDatabase = Room.databaseBuilder(
        context,
        FlagsDatabase::class.java,
        "flags-database",
    ).build()
}