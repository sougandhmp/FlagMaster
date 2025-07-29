package org.smp.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.smp.data.repository.FlagsRepositoryImpl
import org.smp.domain.repository.FlagsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFlagsRepository(flagsRepository: FlagsRepositoryImpl): FlagsRepository
}
