package org.smp.data.sync.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.smp.data.sync.FirebaseBackgroundSyncManager
import org.smp.data.sync.NetworkStateManager
import org.smp.data.sync.NetworkStateManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideFirebaseBackgroundSyncManager(
        @ApplicationContext context: Context
    ): FirebaseBackgroundSyncManager {
        return FirebaseBackgroundSyncManager(context)
    }

    @Provides
    @Singleton
    fun provideNetworkStateManager(
        @ApplicationContext context: Context
    ): NetworkStateManager {
        return NetworkStateManagerImpl(context)
    }
}
