package raf.rma.catapult.leaderboard.repository

import raf.rma.catapult.leaderboard.api.LeaderboardApi
import raf.rma.catapult.leaderboard.api.model.LeaderboardPost
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
) {
    suspend fun fetchLeaderboard() = leaderboardApi.fetchLeaderboard()
    suspend fun postLeaderboard(leaderboardPost : LeaderboardPost) =
        leaderboardApi.postLeaderboard(leaderboardPost)
}