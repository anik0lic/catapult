package raf.rma.catapult.navigation

import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import raf.rma.catapult.cats.details.catDetails
import raf.rma.catapult.cats.list.cats
import raf.rma.catapult.leaderboard.list.leaderboard
import raf.rma.catapult.photos.gallery.photoGallery
import raf.rma.catapult.photos.grid.photoGrid
import raf.rma.catapult.profile.datastore.ProfileDataStore
import raf.rma.catapult.profile.details.profile
import raf.rma.catapult.profile.login.login
import raf.rma.catapult.quiz.ui.quiz

@Composable
fun AppNavigation(profileDataStore: ProfileDataStore) {
    val navController = rememberNavController()
    val isProfileEmpty by profileDataStore.isEmpty.collectAsState(initial = true)
    var startDest by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            startDest = if (isProfileEmpty) "login" else "cats"
        }
    }

    if (startDest == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else{
    NavHost(
        navController = navController,
        startDestination = startDest!!,
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
            login(
                route = "login",
            )

            cats(
                route = "cats",
                onCatClick = {
                    navController.navigate(route = "cats/details/$it")
                },
                onProfileClick = {
                    navController.navigate(route = "profile")
                },
                onQuizClick = {
                    navController.navigate(route = "quiz")
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
                    navArgument(name = "catId") {
                        nullable = false
                        type = NavType.StringType
                    }
                ),
                onClose = {
                    navController.navigateUp()
                },
            )

            quiz(
                route = "quiz",
                onQuizCompleted = {
                    navController.navigate(route = "cats")
                },
                onClose = {
                    navController.navigateUp()
                }
            )


            leaderboard(
                route = "leaderboard",
                onCatalogClick = {
                    navController.navigate(route = "cats")
                },
                onProfileClick = {
                    navController.navigate(route = "profile")
                },
                onQuizClick = {
                    navController.navigate(route = "quiz")
                },
            )

            profile(
                route = "profile",
                onCatalogClick = {
                    navController.navigate(route = "cats")
                },
                onQuizClick = {
                    navController.navigate(route = "quiz")
                },
                onLeaderboardClick = {
                    navController.navigate(route = "leaderboard")
                },
            )
        }

    }
}

inline val SavedStateHandle.catId: String
    get() = checkNotNull(get("catId")) { "catId is mandatory" }

inline val SavedStateHandle.photoId: String
    get() = checkNotNull(get("photoId")) { "photoId is mandatory" }