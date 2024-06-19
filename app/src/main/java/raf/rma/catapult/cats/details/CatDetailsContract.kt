package raf.rma.catapult.cats.details

import raf.rma.catapult.cats.model.CatUiModel
import raf.rma.catapult.photos.model.PhotoUiModel

interface CatDetailsContract {
    data class CatDetailsState(
        val loading: Boolean = true,
        val cat: CatUiModel? = null,
        val photo: PhotoUiModel? = null,
        val error: Boolean = false
    )
}