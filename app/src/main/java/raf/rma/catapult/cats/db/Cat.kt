package raf.rma.catapult.cats.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import raf.rma.catapult.cats.api.model.Weight

@Entity
data class Cat (
    @PrimaryKey val id: String,
    val name: String,
    val temperament: String,
    val origin: String,
    val description: String,
    val lifeSpan: String,
    @Embedded val weight: Weight,
    val rare: Int,

    val adaptability: Int,
    val affectionLevel: Int,
    val intelligence: Int,
    val childFriendly: Int,
    val socialNeeds: Int,

    val referenceImageId: String = ""
)