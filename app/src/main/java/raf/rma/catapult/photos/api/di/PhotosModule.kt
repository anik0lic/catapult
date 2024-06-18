package raf.rma.catapult.photos.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import raf.rma.catapult.photos.api.PhotosApi
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhotosModule {

    @Provides
    @Singleton
    fun providePhotosApi(@Named("cat") retrofit: Retrofit): PhotosApi = retrofit.create()
}