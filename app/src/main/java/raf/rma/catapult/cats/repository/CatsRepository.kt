package raf.rma.catapult.cats.repository

import raf.rma.catapult.database.AppDatabase
import raf.rma.catapult.cats.api.CatsApi
import raf.rma.catapult.cats.mappers.asCatDbModel
import javax.inject.Inject

class CatsRepository @Inject constructor(
    private val catsApi: CatsApi,
    private val database: AppDatabase
){

    suspend fun fetchAllCats() {
        val cats = catsApi.fetchAllCats()
        database.catsDao().insertAll(list = cats.map { it.asCatDbModel() })
    }

    suspend fun getCatDetails(catId: String){
        database.catsDao().getCatById(catId = catId)
    }

    fun observeCats() = database.catsDao().observeCats()
    fun observeCatDetails(catId: String) = database.catsDao().observeCatDetails(catId = catId)
//
//    suspend fun getUserWithAlbums(userId: Int): UserWithAlbums {
//        return database.userDao().getUserWithAlbums(userId = userId)
//    }
}