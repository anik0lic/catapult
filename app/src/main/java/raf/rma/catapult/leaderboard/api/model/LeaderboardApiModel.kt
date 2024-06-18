package raf.rma.catapult.leaderboard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardApiModel(
    val nickname: String = "",
    val result: Float = 0f
)
