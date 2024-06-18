package raf.rma.catapult.cats.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rma.catapult.cats.list.CatListContract.CatListUiEvent
import raf.rma.catapult.cats.list.CatListContract.CatListState
import raf.rma.catapult.cats.mappers.asCatUiModel
import raf.rma.catapult.cats.repository.CatsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatsRepository
) : ViewModel(){

    private val _state = MutableStateFlow(CatListState())
    val state = _state.asStateFlow()
    private fun setState(reducer: CatListState.() -> CatListState) = _state.update(reducer)

    private val events = MutableSharedFlow<CatListUiEvent>()
    fun setEvent(event: CatListUiEvent) = viewModelScope.launch { events.emit(event) }

    init {
        observeEvents()
        fetchAllCats()
        observeCats()
        observeSearchQuery()
    }

    private fun observeCats(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            repository.observeCats()
                .distinctUntilChanged()
                .collect {
                    setState {
                        copy(
                            loading = false,
                            cats = it.map { it.asCatUiModel() }
                        )
                    }
                }
        }
    }

    private fun fetchAllCats(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchAllCats()
                    Log.e("FETCH", "Fetch Cats")
                }
            } catch (error: Exception) {
                Log.e("FETCH", "Fetch Cats Error", error)
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect {
                when (it) {
                    CatListUiEvent.CloseSearchMode -> setState { copy(isSearchMode = false) }

                    is CatListUiEvent.SearchQueryChanged -> {
                        println("search query changed")
                        println(it.query)
                        setState { copy(query = it.query) }
                        setState { copy(isSearchMode = true) }
                        setState { copy(loading = true) }
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery(){
        viewModelScope.launch {
            events
                .filterIsInstance<CatListUiEvent.SearchQueryChanged>()
                .debounce(1.seconds)
                .collect {
                    setState { copy(filteredCats = state.value.cats.filter { item ->
                        item.name.contains(state.value.query, ignoreCase = true)
                    }) }

                    setState { copy(loading = false) }
                }
        }
    }
}