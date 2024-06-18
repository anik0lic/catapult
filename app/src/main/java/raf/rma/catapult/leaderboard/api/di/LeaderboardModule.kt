package raf.rma.catapult.leaderboard.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import raf.rma.catapult.leaderboard.api.LeaderboardApi
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeaderboardModule {

    @Provides
    @Singleton
    fun provideLeaderboardApi(@Named("leaderboard") retrofit: Retrofit): LeaderboardApi = retrofit.create()
}