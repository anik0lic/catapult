package raf.rma.catapult.photos.grid

import raf.rma.catapult.photos.model.PhotoUiModel

interface PhotoGridContract {
    data class PhotoGridState(
        val updating: Boolean = false,
        val photos: List<PhotoUiModel> = emptyList(),
    )
}