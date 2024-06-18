package raf.rma.catapult.leaderboard.repository

import raf.rma.catapult.leaderboard.api.LeaderboardApi
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
) {

    suspend fun fetchLeaderboard() = leaderboardApi.fetchLeaderboard()
}