package raf.rma.catapult.cats.details

import raf.rma.catapult.cats.model.CatUiModel

interface CatDetailsContract {
    data class CatDetailsState(
        val loading: Boolean = true,
        val cat: CatUiModel? = null,
        val error: Boolean = false
    )
}