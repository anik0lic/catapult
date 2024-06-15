package raf.rma.catapult.photos.gallery

import raf.rma.catapult.photos.gallery.model.PhotoUiModel

interface PhotoGalleryContract {
    data class AlbumGalleryUiState(
        val photos: List<PhotoUiModel> = emptyList(),
    )
}