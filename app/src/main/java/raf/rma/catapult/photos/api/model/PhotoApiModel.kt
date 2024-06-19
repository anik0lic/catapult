package raf.rma.catapult.photos.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoApiModel (
    @SerialName("id") val photoId: String,
    val url: String,
    val width: Int,
    val height: Int,
)