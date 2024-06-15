package raf.rma.catapult.photos.albums.grid

import raf.rma.catapult.photos.albums.model.AlbumUiModel

interface AlbumGridContract {
    data class AlbumGridUiState(
        val updating: Boolean = false,
        val albums: List<AlbumUiModel> = emptyList(),
    )
}