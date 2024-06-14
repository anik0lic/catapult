package raf.rma.catapult.photos.repository

import androidx.room.withTransaction
import raf.rma.catapult.database.AppDatabase
import raf.rma.catapult.photos.api.PhotosApi
import raf.rma.catapult.photos.db.Photo
import raf.rma.catapult.photos.mappers.asAlbumDbModel
import raf.rma.catapult.photos.mappers.asPhotoDbModel
import javax.inject.Inject

class PhotosRepository @Inject constructor(
    private val photosApi: PhotosApi,
    private val database: AppDatabase,
) {
    suspend fun fetchUserAlbums(userId: Int) {
        val allAlbums = photosApi.getAlbums(userId = userId)
            .map { it.asAlbumDbModel() }
            .toMutableList()

        val allPhotos = mutableListOf<Photo>()
        allAlbums.forEachIndexed { index, album ->
            val albumPhotos = photosApi.getAlbumPhotos(albumId = album.albumId)
                .map { it.asPhotoDbModel(userId = userId) }

            allPhotos.addAll(albumPhotos)
            albumPhotos.firstOrNull()?.let {
                allAlbums[index] = album.copy(
                    coverUrl = it.url,
                    coverThumbnailUrl = it.thumbnailUrl,
                )
            }
        }

        database.withTransaction {
            database.albumDao().upsertAllPhotos(data = allPhotos)
            database.albumDao().upsertAllAlbums(data = allAlbums)
        }
    }

    fun observeUserAlbums(userId: Int) = database.albumDao().observeUserAlbums(userId = userId)

    fun observeAlbumPhotos(albumId: Int) = database.albumDao().observeAlbumPhotos(albumId = albumId)

    suspend fun deleteAlbum(albumId: Int) = photosApi.deleteAlbum(albumId = albumId)
}