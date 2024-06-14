package raf.rma.catapult.photos.mappers

import raf.rma.catapult.photos.api.model.AlbumApiModel
import raf.rma.catapult.photos.api.model.PhotoApiModel
import raf.rma.catapult.photos.db.Album
import raf.rma.catapult.photos.db.Photo

fun AlbumApiModel.asAlbumDbModel(): Album {
    return Album(
        albumId = this.albumId,
        userOwnerId = this.userId,
        title = this.title,
        coverUrl = null,
        coverThumbnailUrl = null,
    )
}

fun PhotoApiModel.asPhotoDbModel(userId: Int): Photo {
    return Photo(
        photoId = this.photoId,
        albumId = this.albumId,
        userOwnerId = userId,
        title = this.title,
        url = this.url,
        thumbnailUrl = this.thumbnailUrl,
    )
}