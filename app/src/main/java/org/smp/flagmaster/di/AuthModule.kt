package org.smp.flagmaster.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.smp.flagmaster.ui.auth.CredentialManagerGoogleAuthProvider
import org.smp.flagmaster.ui.auth.GoogleAuthProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindGoogleAuthProvider(impl: CredentialManagerGoogleAuthProvider): GoogleAuthProvider
}
