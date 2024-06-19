package raf.rma.catapult.photos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM Photo")
    suspend fun getAll(): List<Photo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Photo>)

    @Query("SELECT * FROM Photo WHERE Photo.catId = :catId")
    fun observeCatPhotos(catId: String): Flow<List<Photo>>

    @Query("SELECT * FROM Photo")
    fun observePhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM Photo WHERE Photo.catId = :catId")
    suspend fun getPhotosByCatId(catId: String): List<Photo>

//    @Query("SELECT * FROM Photo WHERE Photo.photoId = :photoId")
//    fun observePhoto(photoId: String): Flow<Photo>
}