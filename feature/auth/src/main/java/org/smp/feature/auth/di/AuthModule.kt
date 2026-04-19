package org.smp.feature.auth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.smp.feature.auth.CredentialManagerGoogleAuthProvider
import org.smp.feature.auth.GoogleAuthProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindGoogleAuthProvider(impl: CredentialManagerGoogleAuthProvider): GoogleAuthProvider
}
