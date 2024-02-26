package app.androcam

sealed class Screen(val route: String) {
    object CameraPreview : Screen(route = "camera_preview")
    object Help : Screen(route = "help")
}
