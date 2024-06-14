package raf.rma.catapult.users.repository

import raf.rma.catapult.database.AppDatabase
import raf.rma.catapult.users.api.UsersApi
import raf.rma.catapult.users.db.UserWithAlbums
import raf.rma.catapult.users.mappers.asUserDbModel
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val database: AppDatabase
){

    suspend fun fetchAllUsers() {
        val users = usersApi.getAllUsers()
        database.userDao().insertAll(list = users.map { it.asUserDbModel() })
    }

    suspend fun observeAllUserProfiles() = database.userDao().observeAllUserProfiles()

    suspend fun getUserWithAlbums(userId: Int): UserWithAlbums {
        return database.userDao().getUserWithAlbums(userId = userId)
    }
}