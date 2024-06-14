package raf.rma.catapult.users.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<UserData>)

    @Query("SELECT * FROM UserData")
    suspend fun getAll(): List<UserData>

    @Transaction
    @Query(
        """
            SELECT UD.id, UD.name, UD.username, COUNT(Album.albumId) as albumsCount
            FROM UserData AS UD
            LEFT JOIN Album ON UD.id = Album.userOwnerId
            GROUP BY UD.id
        """
    )
    fun observeAllUserProfiles(): Flow<List<UserProfile>>

    @Transaction
    @Query("SELECT * FROM UserData WHERE id = :userId")
    suspend fun getUserWithAlbums(userId: Int): UserWithAlbums
}