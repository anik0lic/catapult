package raf.rma.catapult.photos.albums.list

import raf.rma.catapult.photos.albums.model.AlbumUiModel

interface AlbumListContract {
    data class AlbumListUiState(
        val loading: Boolean = false,
        val albums: List<AlbumUiModel> = emptyList(),
    )

    sealed class AlbumListUiEvent {
        data class DeleteAlbum(val albumId: Int) : AlbumListUiEvent()
    }
}