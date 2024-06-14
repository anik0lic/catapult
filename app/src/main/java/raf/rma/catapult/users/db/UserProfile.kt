package raf.rma.catapult.users.db

data class UserProfile (
    val id: Int,
    val name: String,
    val username: String,
    val albumsCount: Int
)