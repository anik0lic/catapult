package raf.rma.catapult.leaderboard.api

import raf.rma.catapult.leaderboard.api.model.LeaderboardApiModel
import raf.rma.catapult.leaderboard.api.model.LeaderboardPost
import raf.rma.catapult.leaderboard.api.model.LeaderboardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LeaderboardApi {

    @GET("leaderboard?category=1")
    suspend fun fetchLeaderboard(): List<LeaderboardApiModel>

    @POST("leaderboard")
    suspend fun postLeaderboard(@Body leaderboardPost: LeaderboardPost) : LeaderboardResponse
}