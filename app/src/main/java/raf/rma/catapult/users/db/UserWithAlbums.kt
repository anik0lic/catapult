package raf.rma.catapult.users.db

import androidx.room.Embedded
import androidx.room.Relation
import raf.rma.catapult.photos.db.Album

data class UserWithAlbums (

    @Embedded val data: UserData,

    @Relation(
        parentColumn = "id",
        entityColumn = "userOwnerId",
    )
    val albums: List<Album>
)