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
import raf.rma.catapult.cats.details.catDetails
import raf.rma.catapult.photos.gallery.photoGallery
import raf.rma.catapult.cats.list.cats
import raf.rma.catapult.leaderboard.list.leaderboard
import raf.rma.catapult.photos.grid.photoGrid

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "cats",
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
        cats(
            route = "cats",
            onCatClick = {
                navController.navigate(route = "cats/details/$it")
            },
            onProfileClick = {

            },
            onQuizClick = {

            },
            onLeaderboardClick = {
                navController.navigate(route = "leaderboard")
            }
        )

        catDetails(
            route = "cats/details/{catId}",
            arguments = listOf(
                navArgument(name = "catId") {
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onGalleryClick = {
                navController.navigate(route = "cats/grid/$it")
            },
            onClose = {
                navController.popBackStack()
            },
        )

        photoGrid(
            route = "cats/grid/{catId}",
            arguments = listOf(
                navArgument(name = "catId") {
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onPhotoClick = {
                navController.navigate(route = "photo/$it")
            },
            onClose = {
                navController.popBackStack()
            },
        )

        photoGallery(
            route = "photo/{catId}",
            arguments = listOf(
//                navArgument(name = "photoId") {
//                    nullable = false
//                    type = NavType.StringType
//                },
                navArgument(name = "catId") {
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onClose = {
                navController.navigateUp()
            },
        )

        leaderboard(
            route = "leaderboard",
            onCatalogClick = {
                navController.navigate(route = "cats")
            },
            onProfileClick = {

            },
            onQuizClick = {

            },
        )

        securityNavigation(
            route = "security",
            navController = navController,
        )
    }
}

inline val SavedStateHandle.catId: String
    get() = checkNotNull(get("catId")) { "catId is mandatory" }

inline val SavedStateHandle.photoId: String
    get() = checkNotNull(get("photoId")) { "photoId is mandatory" }