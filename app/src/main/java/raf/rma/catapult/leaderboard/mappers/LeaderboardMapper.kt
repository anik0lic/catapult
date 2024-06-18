package raf.rma.catapult.leaderboard.mappers

import raf.rma.catapult.leaderboard.api.model.LeaderboardApiModel
import raf.rma.catapult.leaderboard.model.LeaderboardUiModel

fun LeaderboardApiModel.asLeaderboardUiModel(id: Int): LeaderboardUiModel {
    return LeaderboardUiModel(
        id = id,
        nickname = this.nickname,
        result = this.result
    )
}