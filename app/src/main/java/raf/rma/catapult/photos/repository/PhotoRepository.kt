package raf.rma.catapult.photos.repository

import androidx.room.withTransaction
import raf.rma.catapult.database.AppDatabase
import raf.rma.catapult.photos.api.PhotosApi
import raf.rma.catapult.photos.mappers.asPhotoDbModel
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photosApi: PhotosApi,
    private val database: AppDatabase,
) {

    suspend fun fetchCatPhotos(catId: String) {
        val allPhotos = photosApi.getPhotos(catId = catId)
            .map { it.asPhotoDbModel(catId) }
            .toMutableList()

        database.withTransaction {
            database.photoDao().insertAll(list = allPhotos)
        }
    }

    fun observeCatPhotos(catId: String) = database.photoDao().observeCatPhotos(catId = catId)

//    fun observePhoto(photoId: String) = database.photoDao().observePhoto(photoId = photoId)
}