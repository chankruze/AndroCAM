package app.androcam

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController


@Composable
fun CameraPreviewScreen(navController: NavController, isConnected: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isConnected) {
            CameraPreview()
        }

        if (!isConnected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_devices_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "Plug this device into your Windows PC via USB to connect",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    // overlay
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(16.dp)
            .statusBarsPadding()
            .zIndex(1f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                ) {
                    PulsatingCircle(Color.Green, 2000)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
            }
            Column {
                val status = when (isConnected) {
                    true -> "Connected"
                    false -> "Not connected"
                }

                Text(
                    text = status,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (isConnected) {
                    Text(
                        text = "DESKTOP-C9HJK45",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End,
        ) {
            FloatingButton(
                icon = ImageVector.vectorResource(id = R.drawable.baseline_question_mark_24),
                onClick = {
                    navController.navigate(Screen.Help.route)
                })
            FloatingButton(
                icon = ImageVector.vectorResource(id = R.drawable.baseline_settings_24),
                onClick = {
                    Log.d("FAB", "Settings clicked")
                })
            FloatingButton(
                icon = ImageVector.vectorResource(id = R.drawable.baseline_wifi_24),
                onClick = {
                    Log.d("FAB", "Wifi clicked")
                })
        }
    }

}

@Composable
fun FloatingButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun PulsatingCircle(color: Color = Color.Red, duration: Int = 1000) {
    val radius by remember { mutableFloatStateOf(50f) }
    val infiniteTransition = rememberInfiniteTransition(label = "infinite_transition")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
                0.2f at 0 with LinearOutSlowInEasing
                1f at (duration / 2) with LinearOutSlowInEasing
                0.2f at duration with LinearOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = color.copy(alpha = alpha),
            center = Offset(size.width / 2, size.height / 2),
            radius = radius
        )
    }
}

@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val cameraProviderFuture =
        ProcessCameraProvider.getInstance(LocalContext.current as ComponentActivity)
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(previewView, cameraProvider, lifecycleOwner)
            }, ContextCompat.getMainExecutor(context))
            previewView
        }
    )
}

fun bindPreview(
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner
) {
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    preview.setSurfaceProvider(previewView.surfaceProvider)

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    } catch (exc: Exception) {
        Log.e("Camera", "Use case binding failed", exc)
    }
}