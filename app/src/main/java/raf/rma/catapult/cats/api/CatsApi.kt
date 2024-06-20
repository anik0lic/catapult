package raf.rma.catapult.cats.api

import raf.rma.catapult.cats.api.model.CatApiModel
import retrofit2.http.GET

interface CatsApi {
    @GET("breeds")
    suspend fun fetchAllCats(): List<CatApiModel>
}