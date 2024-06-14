package raf.rma.catapult.photos.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo (
    @PrimaryKey val photoId: Int,
    val albumId: Int,
    val userOwnerId: Int? = null,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)