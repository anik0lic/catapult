package raf.rma.catapult.photos.mappers

import raf.rma.catapult.photos.api.model.PhotoApiModel
import raf.rma.catapult.photos.db.Photo
import raf.rma.catapult.photos.model.PhotoUiModel

fun PhotoApiModel.asPhotoDbModel(catId: String): Photo {
    return Photo(
        photoId = this.photoId,
        catId = catId,
        url = this.url,
        width = this.width,
        height = this.height,
    )
}

fun Photo.asPhotoUiModel(catId: String): PhotoUiModel {
    return PhotoUiModel(
        photoId = this.photoId,
        catId = catId,
        url = this.url,
        width = this.width,
        height = this.height,
    )
}