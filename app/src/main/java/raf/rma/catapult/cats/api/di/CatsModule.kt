package raf.rma.catapult.cats.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import raf.rma.catapult.cats.api.CatsApi
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CatsModule {

    @Provides
    @Singleton
    fun provideCatsApi(@Named("cat") retrofit: Retrofit): CatsApi = retrofit.create()
}