package raf.rma.catapult.photos.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumApiModel (
    @SerialName("id") val albumId: Int,
    val userId: Int,
    val title: String,
)