package raf.rma.catapult.profile.details

import raf.rma.catapult.profile.datastore.ProfileData
import raf.rma.catapult.quiz.db.QuizResult

interface ProfileContract {
    data class ProfileState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
        val quizResults: List<QuizResult> = emptyList(),
        val bestScore: Float = 0f,
        val bestPosition: Int = 0
    )
    sealed class ProfileEvent {
        data class EditProfile(val name: String, val nickname: String, val email: String) : ProfileEvent()
    }
}