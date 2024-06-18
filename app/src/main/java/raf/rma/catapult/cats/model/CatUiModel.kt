package raf.rma.catapult.cats.model

import raf.rma.catapult.cats.api.model.Weight

data class CatUiModel (
    val id: String,
    val name: String,
    val temperament: String,
    val origin: String,
    val description: String,
    val lifeSpan: String,
    val weight: Weight,
    val rare: Int,

    val adaptability: Int,
    val affectionLevel: Int,
    val intelligence: Int,
    val socialNeeds: Int,
    val childFriendly: Int,

    val referenceImageId: String = ""
)