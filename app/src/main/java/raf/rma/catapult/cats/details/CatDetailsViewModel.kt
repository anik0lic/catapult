package raf.rma.catapult.cats.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rma.catapult.cats.details.CatDetailsContract.CatDetailsState
import raf.rma.catapult.cats.mappers.asCatUiModel
import raf.rma.catapult.cats.repository.CatsRepository
import raf.rma.catapult.navigation.catId
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CatsRepository
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
                    repository.getCatDetails(catId = catId)
                }
            } catch (error: Exception) {
                setState { copy(error = true) }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

    private fun observeCatDetails() {
        viewModelScope.launch {
            repository.observeCatDetails(catId = catId)
                .collect {
                    setState { copy(cat = it.asCatUiModel()) }
                }
        }
    }
}