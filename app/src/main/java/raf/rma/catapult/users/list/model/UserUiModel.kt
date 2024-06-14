package raf.rma.catapult.users.list.model

data class UserUiModel (
    val id: Int,
    val name: String,
    val username: String,
    val albumsCount: Int?
)