package raf.rma.catapult.users.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UserApiModel(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Address,
    val phone: String,
    val website: String,
)

@Serializable
data class Address(
    val street: String,
    val city: String,
    val zipcode: String,
)