package app.androcam

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController, startDestination = Screen.CameraPreview.route
    ) {
        // destination 1
        composable(route = Screen.CameraPreview.route, enterTransition = {
            when (initialState.destination.route) {
                Screen.Help.route -> slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )

                else -> null
            }
        }) {
            CameraPreviewScreen(navController = navController, true)
        }

        // destination 2
        composable(route = Screen.Help.route, enterTransition = {
            when (initialState.destination.route) {
                Screen.CameraPreview.route -> slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )

                else -> null
            }
        }) {
            HelpScreen(navController = navController)
        }
    }
}