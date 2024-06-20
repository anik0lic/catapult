package raf.rma.catapult.cats.details

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
import raf.rma.catapult.cats.details.CatDetailsContract.CatDetailsState
import raf.rma.catapult.cats.mappers.asCatUiModel
import raf.rma.catapult.cats.repository.CatsRepository
import raf.rma.catapult.navigation.catId
import raf.rma.catapult.photos.mappers.asPhotoUiModel
import raf.rma.catapult.photos.repository.PhotoRepository
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catsRepository: CatsRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val catId: String = savedStateHandle.catId

    private val _state = MutableStateFlow(CatDetailsState())
    val state = _state.asStateFlow()
    private fun setState(reducer: CatDetailsState.() -> CatDetailsState) = _state.update(reducer)

    init {
        fetchCatDetails()
        observeCatDetails()
    }

    private fun fetchCatDetails(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    catsRepository.getCatDetails(catId = catId)
                }
            } catch (error: Exception) {
                setState { copy(error = true) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

    private fun fetchImage(photoId: String, catId: String){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    photoRepository.fetchPhoto(photoId = photoId, catId = catId)
                }
                getImage()
            } catch (error: IOException) {
                setState { copy(error = true) }
            }
        }
    }

    private fun getImage(){
        viewModelScope.launch {
            try {
                val photo = withContext(Dispatchers.IO) {
                    photoRepository.getPhotosByCatId(catId)[0].asPhotoUiModel()
                }
                setState { copy(photo = photo) }
            } catch (error: IOException) {
                setState { copy(error = true) }
            }
        }
    }

    private fun observeCatDetails() {
        viewModelScope.launch {
            catsRepository.observeCatDetails(catId = catId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(cat = it.asCatUiModel()) }
                    fetchImage(it.referenceImageId, it.id)
                }
        }
    }

}