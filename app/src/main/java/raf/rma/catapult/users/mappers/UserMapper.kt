package raf.rma.catapult.users.mappers

import raf.rma.catapult.users.api.model.UserApiModel
import raf.rma.catapult.users.db.UserData

fun UserApiModel.asUserDbModel(): UserData {
    return UserData(
        id = this.id,
        name = this.name,
        username = this.username,
        email = this.email,
        address = this.address,
        phone = this.phone,
        website = this.website,
    )
}