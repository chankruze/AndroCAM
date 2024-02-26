package app.androcam

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
        composable(route = Screen.CameraPreview.route) {
            CameraPreviewScreen(navController = navController)
        }

        // destination 2
        composable(route = Screen.Help.route) {
            HelpScreen(navController = navController)
        }
    }
}