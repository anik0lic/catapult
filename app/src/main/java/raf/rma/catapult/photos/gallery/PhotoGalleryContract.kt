package raf.rma.catapult.photos.gallery

import raf.rma.catapult.photos.model.PhotoUiModel

interface PhotoGalleryContract {
    data class PhotoGalleryState(
        val photos: List<PhotoUiModel> = emptyList(),
    )
}