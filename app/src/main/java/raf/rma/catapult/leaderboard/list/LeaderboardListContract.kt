package raf.rma.catapult.leaderboard.list

import raf.rma.catapult.leaderboard.model.LeaderboardUiModel

interface LeaderboardListContract {
    data class LeaderboardListState(
        val loading: Boolean = true,
        val results: List<LeaderboardUiModel> = emptyList(),
        val error: Boolean = false,
    )
}