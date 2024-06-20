package raf.rma.catapult.leaderboard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardPost(
    val nickname: String,
    val result: Float,
    val category: Int,
)

@Serializable
data class LeaderboardResponse(
    val result: LeaderboardApiModel,
    val ranking: Int
)
