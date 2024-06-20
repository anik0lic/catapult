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
        val allPhotos = photosApi.fetchPhotos(catId = catId)
            .map { it.asPhotoDbModel(catId) }
            .toMutableList()

        database.withTransaction {
            database.photoDao().insertAll(list = allPhotos)
        }
    }

    suspend fun fetchPhoto(photoId: String, catId: String) {
        val photo = photosApi.fetchPhotoById(photoId = photoId).asPhotoDbModel(catId)
        database.photoDao().insert(photo)
    }

    suspend fun getAllPhotos() = database.photoDao().getAll()

    suspend fun getPhotosByCatId(catId: String) = database.photoDao().getPhotosByCatId(catId = catId)

    fun observeCatPhotos(catId: String) = database.photoDao().observeCatPhotos(catId = catId)
}