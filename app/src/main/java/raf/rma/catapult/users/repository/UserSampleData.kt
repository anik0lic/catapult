package raf.rma.catapult.users.repository

import raf.rma.catapult.users.list.model.UserUiModel

val SampleData = listOf(
    UserUiModel(
        id = 1,
        name = "John Doe",
        username = "john.doe",
        albumsCount = 3
    ),
    UserUiModel(
        id = 2,
        name = "Jane Doe",
        username = "jane.doe",
        albumsCount = 2
    ),
    UserUiModel(
        id = 3,
        name = "John Smith",
        username = "john.smith",
        albumsCount = 1
    ),
)