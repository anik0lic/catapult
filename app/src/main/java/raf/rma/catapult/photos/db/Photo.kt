package raf.rma.catapult.photos.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo (
    @PrimaryKey val photoId: String,
    val catId: String,
    val url: String? = "",
    val width: Int,
    val height: Int,
)