package raf.rma.catapult.photos.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album (
    @PrimaryKey val albumId: Int,
    val userOwnerId: Int,
    val title: String,
    val coverThumbnailUrl: String?,
    val coverUrl: String?,
)