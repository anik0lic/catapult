package raf.rma.catapult.cats.mappers

import raf.rma.catapult.cats.api.model.CatApiModel
import raf.rma.catapult.cats.db.Cat
import raf.rma.catapult.cats.model.CatUiModel

fun CatApiModel.asCatDbModel(): Cat {
    return Cat(
        id = this.id,
        name = this.name,
        temperament = this.temperament,
        origin = this.origin,
        description = this.description,
        lifeSpan = this.lifeSpan,
        weight = this.weight,
        rare = this.rare,
        adaptability = this.adaptability,
        affectionLevel = this.affectionLevel,
        intelligence = this.intelligence,
        childFriendly = this.childFriendly,
        socialNeeds = this.socialNeeds,
        referenceImageId = this.referenceImageId
    )
}

fun Cat.asCatUiModel(): CatUiModel {
    return CatUiModel(
        id = this.id,
        name = this.name,
        temperament = this.temperament,
        origin = this.origin,
        description = this.description,
        lifeSpan = this.lifeSpan,
        weight = this.weight,
        rare = this.rare,
        adaptability = this.adaptability,
        affectionLevel = this.affectionLevel,
        intelligence = this.intelligence,
        childFriendly = this.childFriendly,
        socialNeeds = this.socialNeeds,
        referenceImageId = this.referenceImageId
    )
}