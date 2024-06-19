package raf.rma.catapult.quiz.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    @Query("SELECT * FROM quiz_results WHERE nickname = :nickname ORDER BY date DESC")
    fun getQuizResultsForUser(nickname: String): Flow<List<QuizResult>>

    @Query("SELECT MAX(score) FROM quiz_results WHERE nickname = :nickname")
    suspend fun getBestScore(nickname: String): Float?

    @Query("SELECT COUNT(*) FROM (SELECT DISTINCT score FROM quiz_results WHERE score > (SELECT MAX(score) FROM quiz_results WHERE nickname = :nickname))")
    suspend fun getBestPosition(nickname: String): Int
}