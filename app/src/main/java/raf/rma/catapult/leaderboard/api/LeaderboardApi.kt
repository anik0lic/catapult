package raf.rma.catapult.leaderboard.api

import raf.rma.catapult.leaderboard.api.model.LeaderboardApiModel
import retrofit2.http.GET

interface LeaderboardApi {

    @GET("leaderboard?category=1")
    suspend fun fetchLeaderboard(): List<LeaderboardApiModel>
}