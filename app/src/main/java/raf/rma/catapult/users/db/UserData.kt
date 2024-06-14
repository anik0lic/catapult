package raf.rma.catapult.users.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import raf.rma.catapult.users.api.model.Address

@Entity
data class UserData (
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    @Embedded val address: Address,
    val phone: String,
    val website: String
)