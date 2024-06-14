package raf.rma.catapult.users.list

import raf.rma.catapult.users.list.model.UserUiModel

interface UserListContract {
    data class UserListState(
        val loading: Boolean = true,
        val updating: Boolean = false,
        val users: List<UserUiModel> = emptyList(),
    )
}