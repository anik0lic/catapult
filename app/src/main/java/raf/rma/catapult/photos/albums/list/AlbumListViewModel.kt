package raf.rma.catapult.photos.albums.list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rma.catapult.navigation.userId
import raf.rma.catapult.photos.albums.list.AlbumListContract.*
import raf.rma.catapult.photos.albums.model.AlbumUiModel
import raf.rma.catapult.photos.db.Album
import raf.rma.catapult.photos.repository.PhotosRepository
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photosRepository: PhotosRepository
) : ViewModel() {

    private val userId: Int = savedStateHandle.userId

    private val _state = MutableStateFlow(AlbumListUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumListUiState.() -> AlbumListUiState) = _state.update(reducer)

    private val events = MutableSharedFlow<AlbumListUiEvent>()
    fun setEvent(event: AlbumListUiEvent) = viewModelScope.launch { events.emit(event) }

    init {
        fetchAlbums()
        observeEvents()
        observeAlbums()
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect {
                when(it){
                    is AlbumListUiEvent.DeleteAlbum -> {
                        handleDeleteEvent(albumId = it.albumId)
                    }
                }
            }
        }

    }

    private fun observeAlbums() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            photosRepository.observeUserAlbums(userId = userId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(albums = it.map { it.asAlbumUiModel() }) }
                }
        }
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    photosRepository.fetchUserAlbums(userId = userId)
                }
            } catch (error: Exception) {
                Log.d("RMA", "Exception", error)
            }
            setState { copy(loading = false) }
        }
    }

    private fun handleDeleteEvent(albumId: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    photosRepository.deleteAlbum(albumId = albumId)
                }
            } catch (error: Exception) {
                Log.d("RMA", "Exception", error)
            }

            setState {
                copy(albums = this.albums.toMutableList().apply {
                    removeIf { it.id == albumId }
                })
            }
        }
    }

    private fun Album.asAlbumUiModel() = AlbumUiModel(
        id = this.albumId,
        title = this.title,
        coverPhotoUrl = this.coverUrl,
    )
}