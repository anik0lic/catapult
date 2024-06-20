package raf.rma.catapult.photos.grid

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rma.catapult.navigation.catId
import raf.rma.catapult.photos.grid.PhotoGridContract.PhotoGridState
import raf.rma.catapult.photos.mappers.asPhotoUiModel
import raf.rma.catapult.photos.repository.PhotoRepository
import javax.inject.Inject

@HiltViewModel
class PhotoGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val catId: String = savedStateHandle.catId

    private val _state = MutableStateFlow(PhotoGridState())
    val state = _state.asStateFlow()
    private fun setState(reducer: PhotoGridState.() -> PhotoGridState) = _state.update(reducer)

    init {
        fetchPhotos()
        observePhotos()
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            setState { copy(updating = true) }
            try {
                withContext(Dispatchers.IO) {
                    photoRepository.fetchCatPhotos(catId = catId)
                    Log.e("FETCH", "Fetch Photos")
                }
            } catch (error: Exception) {
                Log.d("FETCH", "Fetch Photos Error", error)
            }
            setState { copy(updating = false) }
        }
    }

    private fun observePhotos() {
        viewModelScope.launch {
            photoRepository.observeCatPhotos(catId = catId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(photos = it.map { it.asPhotoUiModel() }) }
                }
        }
    }
}