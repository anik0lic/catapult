package raf.rma.catapult.profile.details

interface ProfileContract {
    data class ProfileState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
//        val quizResults: List<QuizResult> = emptyList(),
        val bestScore: Float = 0f,
        val bestPosition: Int = 0
    )
}