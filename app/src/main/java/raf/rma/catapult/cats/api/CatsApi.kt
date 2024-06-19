package raf.rma.catapult.cats.api

import raf.rma.catapult.cats.api.model.CatApiModel
import raf.rma.catapult.photos.api.model.PhotoApiModel
import retrofit2.http.GET
import retrofit2.http.Path

interface CatsApi {

    @GET("breeds")
    suspend fun fetchAllCats(): List<CatApiModel>

//    @GET("cats/{id}")
//    suspend fun fetchCat(
//        @Path("id") catId: String,
//    ): CatApiModel
}