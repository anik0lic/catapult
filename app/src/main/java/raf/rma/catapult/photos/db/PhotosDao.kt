package raf.rma.catapult.photos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import raf.rma.catapult.cats.db.Cat

@Dao
interface PhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Photo>)

    @Query("SELECT * FROM Photo WHERE Photo.catId = :catId")
    fun observeCatPhotos(catId: String): Flow<List<Photo>>

//    @Query("SELECT * FROM Photo WHERE Photo.photoId = :photoId")
//    fun observePhoto(photoId: String): Flow<Photo>
}