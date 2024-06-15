package raf.rma.catapult.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.securityNavigation(
    route: String,
    navController: NavController,
) = navigation(
    route = route,
    startDestination = "passwords",
) {
//    passwordsListScreen(
//        route = "passwords",
//        navController = navController,
//    )
//
//    passwordDetails(
//        route = "passwords/{id}",
//        navController = navController,
//    )
//
//    passwordsEditor(
//        route = "passwords/editor?id={dataId}",
//        arguments = listOf(
//            navArgument(name = "dataId") {
//                this.type = NavType.StringType
//                this.nullable = true
//            }
//        ),
//        navController = navController,
//    )
}