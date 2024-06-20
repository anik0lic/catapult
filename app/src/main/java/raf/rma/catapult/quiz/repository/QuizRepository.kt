package raf.rma.catapult.quiz.repository

import raf.rma.catapult.database.AppDatabase
import raf.rma.catapult.quiz.db.QuizResult
import javax.inject.Inject

class QuizRepository @Inject constructor(
    private val database: AppDatabase
){
    suspend fun insertQuizResult(result: QuizResult) {
        database.quizResultDao().insertQuizResult(result)
    }

    fun getAllQuizResults(nickname: String) = database.quizResultDao().getQuizResultsForUser(nickname)

    suspend fun getBestScore(userId: String) = database.quizResultDao().getBestScore(userId)

    suspend fun getBestPosition(userId: String) = database.quizResultDao().getBestRanking(userId)

}