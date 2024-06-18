package raf.rma.catapult.cats.list

import raf.rma.catapult.cats.model.CatUiModel

interface CatListContract {
    data class CatListState(
        val loading: Boolean = true,
        val cats: List<CatUiModel> = emptyList(),
        val isSearchMode: Boolean = false,
        val filteredCats: List<CatUiModel> = emptyList(),
        val query: String = "",
        val error: Boolean = false,
    )

    sealed class CatListUiEvent {
        data class SearchQueryChanged(val query: String) : CatListUiEvent()
        data object CloseSearchMode : CatListUiEvent()
    }
}