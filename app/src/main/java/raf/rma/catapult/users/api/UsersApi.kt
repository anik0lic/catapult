package raf.rma.catapult.users.api

import raf.rma.catapult.photos.api.model.AlbumApiModel
import raf.rma.catapult.users.api.model.UserApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersApi {

    @GET("users")
    suspend fun getAllUsers(): List<UserApiModel>

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") userId: Int,
    ): UserApiModel

    @GET("users/{id}/albums")
    suspend fun getUserAlbums(
        @Path("id") userId: Int,
    ): List<AlbumApiModel>
}