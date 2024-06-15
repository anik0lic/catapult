package raf.rma.catapult.photos.albums.grid

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
import raf.rma.catapult.navigation.userId
import raf.rma.catapult.photos.albums.grid.AlbumGridContract.AlbumGridUiState
import raf.rma.catapult.photos.albums.model.AlbumUiModel
import raf.rma.catapult.photos.db.Album
import raf.rma.catapult.photos.repository.PhotosRepository
import javax.inject.Inject

@HiltViewModel
class AlbumGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotosRepository,
) : ViewModel() {

    private val userId: Int = savedStateHandle.userId

    private val _state = MutableStateFlow(AlbumGridUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumGridUiState.() -> AlbumGridUiState) = _state.update(reducer)

    init {
        fetchAlbums()
        observeAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            setState { copy(updating = true) }
            try {
                withContext(Dispatchers.IO) {
                    photoRepository.fetchUserAlbums(userId = userId)
                }
            } catch (error: Exception) {
                Log.d("RMA", "Exception", error)
            }
            setState { copy(updating = false) }
        }
    }

    private fun observeAlbums() {
        viewModelScope.launch {
            photoRepository.observeUserAlbums(userId = userId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(albums = it.map { it.asAlbumUiModel() }) }
                }
        }
    }

    private fun Album.asAlbumUiModel() = AlbumUiModel(
        id = this.albumId,
        title = this.title,
        coverPhotoUrl = this.coverUrl,
    )
}