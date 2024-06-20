package raf.rma.catapult.cats.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cat: Cat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Cat>)

    @Query("SELECT * FROM Cat")
    suspend fun getAll(): List<Cat>

    @Query("SELECT * FROM Cat WHERE id = :catId")
    suspend fun getCatById(catId: String): Cat

    @Query("SELECT * FROM Cat")
    fun observeCats(): Flow<List<Cat>>

    @Query("SELECT * FROM Cat WHERE id = :catId")
    fun observeCatDetails(catId: String): Flow<Cat>

}