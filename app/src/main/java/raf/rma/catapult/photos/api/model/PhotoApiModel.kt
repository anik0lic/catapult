package raf.rma.catapult.photos.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoApiModel (
    @SerialName("id") val photoId: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)