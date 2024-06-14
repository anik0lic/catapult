package raf.rma.catapult.photos.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {

    @Upsert
    fun upsertAllAlbums(data: List<Album>)

    @Upsert
    fun upsertAllPhotos(data: List<Photo>)

    @Query("SELECT * FROM Album WHERE Album.userOwnerId = :userId")
    fun observeUserAlbums(userId: Int): Flow<List<Album>>

    @Query("SELECT * FROM Photo WHERE Photo.albumId = :albumId")
    fun observeAlbumPhotos(albumId: Int): Flow<List<Photo>>
}