package org.smp.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.smp.data.auth.FirebaseAuthRepository
import org.smp.data.repository.FlagsRepositoryImpl
import org.smp.domain.repository.AuthRepository
import org.smp.domain.repository.FlagsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFlagsRepository(flagsRepository: FlagsRepositoryImpl): FlagsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepository: FirebaseAuthRepository): AuthRepository
}
