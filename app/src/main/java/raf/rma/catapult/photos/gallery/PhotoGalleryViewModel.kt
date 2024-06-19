package raf.rma.catapult.photos.gallery

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import raf.rma.catapult.navigation.catId
import raf.rma.catapult.navigation.photoId
import raf.rma.catapult.photos.gallery.PhotoGalleryContract.PhotoGalleryState
import raf.rma.catapult.photos.mappers.asPhotoUiModel
import raf.rma.catapult.photos.repository.PhotoRepository
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository,
) : ViewModel() {

//    private val photoId = savedStateHandle.photoId
    private val catId = savedStateHandle.catId

    private val _state = MutableStateFlow(PhotoGalleryState())
    val state = _state.asStateFlow()
    private fun setState(reducer: PhotoGalleryState.() -> PhotoGalleryState) = _state.update(reducer)

    init {
        observeCatPhotos()
    }

    private fun observeCatPhotos() {
        viewModelScope.launch {
            photoRepository.observeCatPhotos(catId = catId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(photos = it.map { it.asPhotoUiModel() }) }
                    Log.e("OBSERVE", "Observe cat photos")
                }
        }
    }

}