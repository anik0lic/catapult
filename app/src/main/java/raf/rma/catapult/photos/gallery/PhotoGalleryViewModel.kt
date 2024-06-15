package raf.rma.catapult.photos.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import raf.rma.catapult.navigation.albumId
import raf.rma.catapult.photos.db.Photo
import raf.rma.catapult.photos.gallery.PhotoGalleryContract.AlbumGalleryUiState
import raf.rma.catapult.photos.gallery.model.PhotoUiModel
import raf.rma.catapult.photos.repository.PhotosRepository
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotosRepository,
) : ViewModel() {

    private val albumId = savedStateHandle.albumId

    private val _state = MutableStateFlow(AlbumGalleryUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumGalleryUiState.() -> AlbumGalleryUiState) = _state.update(reducer)

    init {
        observeAlbums()
    }

    private fun observeAlbums() {
        viewModelScope.launch {
            photoRepository.observeAlbumPhotos(albumId = albumId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(photos = it.map { it.asPhotoUiModel() }) }
                }
        }
    }

    private fun Photo.asPhotoUiModel() = PhotoUiModel(
        photoId = this.photoId,
        title = this.title,
        url = this.url,
        thumbnailUrl = this.thumbnailUrl,
    )
}