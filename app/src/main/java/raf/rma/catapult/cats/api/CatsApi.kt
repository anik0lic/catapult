package raf.rma.catapult.cats.api

import raf.rma.catapult.cats.api.model.CatApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface CatsApi {

    @GET("breeds")
    suspend fun fetchAllCats(): List<CatApiModel>

//    @GET("cats/{id}")
//    suspend fun fetchCat(
//        @Path("id") catId: String,
//    ): CatApiModel

//    @GET("users/{id}/albums")
//    suspend fun getUserAlbums(
//        @Path("id") userId: Int,
//    ): List<AlbumApiModel>
}