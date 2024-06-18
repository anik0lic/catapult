package raf.rma.catapult.photos.api

import raf.rma.catapult.photos.api.model.PhotoApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotosApi {

    @GET("images/search?limit=25&format=json")
    suspend fun getPhotos(@Query("breed_ids") catId: String): List<PhotoApiModel>

//    @GET("photos")
//    suspend fun getAllPhotos(): List<PhotoApiModel>
//
//    @GET("photos/{id}")
//    suspend fun getPhoto(
//        @Path("id") photoId: Int,
//    ): List<PhotoApiModel>
//
//    @GET("albums")
//    suspend fun getAlbums(): List<AlbumApiModel>
//
//    @GET("albums/{id}")
//    suspend fun getAlbum(
//        @Path("id") albumId: Int,
//    ): List<AlbumApiModel>
//
//    @GET("albums/{id}/photos")
//    suspend fun getAlbumPhotos(
//        @Path("id") albumId: Int,
//    ): List<PhotoApiModel>
//
//    @GET("albums")
//    suspend fun getAlbums(
//        @Query("userId") userId: Int,
//    ): List<AlbumApiModel>
//
//    @DELETE("albums/{id}")
//    suspend fun deleteAlbum(
//        @Path("id") albumId: Int,
//    )
}