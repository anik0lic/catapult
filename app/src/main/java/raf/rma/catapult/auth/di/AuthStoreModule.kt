package raf.rma.catapult.auth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import raf.rma.catapult.auth.AuthDataSerializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthStoreModule {

    @Provides
    @Singleton
    fun provideAuthDataStore(
        @ApplicationContext context: Context,
    ): DataStore<String> =
        DataStoreFactory.create(
            produceFile = { context.dataStoreFile("auth.txt") },
            serializer = AuthDataSerializer(),
    )
}