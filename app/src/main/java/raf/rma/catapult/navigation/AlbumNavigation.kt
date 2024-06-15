package raf.rma.catapult.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import raf.rma.catapult.photos.albums.grid.userAlbumsGrid
import raf.rma.catapult.photos.gallery.photoGallery
import raf.rma.catapult.users.list.users

@Composable
fun AlbumNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "users",
        enterTransition = {
            slideInHorizontally(
                animationSpec = spring(),
                initialOffsetX = { it },
            )
        },
        exitTransition = { scaleOut(targetScale = 0.75f) },
        popEnterTransition = { scaleIn(initialScale = 0.75f) },
        popExitTransition = { slideOutHorizontally { it } },
    ) {
        users(
            route = "users",
            onUserClick = {
                navController.navigate(route = "users/grid/$it")
            },
            onProfileClick = {

            },
            onPasswordsClick = {
                navController.navigate(route = "security")
            },
        )

        userAlbumsGrid(
            route = "users/grid/{userId}",
            arguments = listOf(
                navArgument(name = "userId") {
                    nullable = false
                    type = NavType.IntType
                }
            ),
            onAlbumClick = {
                navController.navigate(route = "users/grid/$it")
            },
            onClose = {
                navController.popBackStack()
            },
        )

        photoGallery(
            route = "albums/{albumId}",
            arguments = listOf(
                navArgument(name = "albumId") {
                    nullable = false
                    type = NavType.IntType
                }
            ),
            onClose = {
                navController.navigateUp()
            },
        )

        securityNavigation(
            route = "security",
            navController = navController,
        )
    }
}

inline val SavedStateHandle.userId: Int
    get() = checkNotNull(get("userId")) { "userId is mandatory" }

inline val SavedStateHandle.albumId: Int
    get() = checkNotNull(get("albumId")) { "albumId is mandatory" }