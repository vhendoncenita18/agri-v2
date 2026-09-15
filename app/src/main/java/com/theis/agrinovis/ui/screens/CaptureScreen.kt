package com.theis.agrinovis.ui.screens

import android.Manifest
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.theis.agrinovis.data.session.MonitoringSession
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.components.CameraPreview
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.NeonGreen
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CaptureScreen(navController: NavController, mode: String) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var captureSuccess by remember { mutableStateOf(false) }
    
    // Preview States
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isPreviewMode by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    // Automatically request permission on entry
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (captureSuccess) {
        LaunchedEffect(Unit) {
            delay(800) // Show success for 800ms
            if (mode == "Diameter") {
                navController.navigate(Screen.CaptureHeight.route)
            } else {
                navController.navigate(Screen.Verification.route)
            }
            captureSuccess = false
            isPreviewMode = false
            capturedImageUri = null
        }
    }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            context.cacheDir,
            "capture_${mode}_${System.currentTimeMillis()}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    capturedImageUri = Uri.fromFile(photoFile)
                    isPreviewMode = true
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                }
            }
        )
    }

    AgrinovisScaffold(
        navController = navController,
        title = "CAPTURE ${mode.uppercase()}",
        showBottomBar = false
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // STEP INDICATOR
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (isPreviewMode) "STEP: REVIEW ${mode.uppercase()}" else "STEP: ANALYZING ${mode.uppercase()}",
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            // CAMERA VIEWFINDER / PREVIEW
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (isPreviewMode && capturedImageUri != null) {
                    AsyncImage(
                        model = capturedImageUri,
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (cameraPermissionState.status.isGranted) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onImageCaptureCreated = { imageCapture = it }
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SENSOR ACCESS REQUIRED",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Please enable camera access to start the AI measurement process.",
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("INITIALIZE CAMERA", color = Color.Black)
                        }
                    }
                }

                // Success Overlay
                if (captureSuccess) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .clip(RoundedCornerShape(50)) // Circular card
                            .background(Color.White)
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "CAPTURED SUCCESSFULLY",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // CONTROLS & INSTRUCTIONS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isPreviewMode)
                        "REVIEW THE CAPTURED IMAGE. ENSURE THE LETTUCE IS CLEARLY VISIBLE FOR AI EXTRACTION."
                    else if (mode == "Diameter")
                        "POSITION CAMERA DIRECTLY OVER THE LETTUCE CORE FOR PRECISION DIAMETER ANALYSIS."
                    else
                        "POSITION CAMERA AT THE BASE LEVEL FOR SIDE-VIEW HEIGHT EXTRACTION.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 18.sp,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (isPreviewMode) {
                                isPreviewMode = false
                                capturedImageUri = null
                            } else {
                                // Already in viewfinder, maybe show a toast or just do nothing
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "RETAKE",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Button(
                        onClick = {
                            if (isPreviewMode) {
                                // Save to session
                                capturedImageUri?.let {
                                    if (mode == "Diameter") {
                                        MonitoringSession.diameterImageUri = it.toString()
                                    } else {
                                        MonitoringSession.heightImageUri = it.toString()
                                    }
                                }
                                captureSuccess = true
                            } else {
                                takePhoto()
                            }
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        enabled = cameraPermissionState.status.isGranted && !captureSuccess,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            disabledContainerColor = NeonGreen.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = if (isPreviewMode) "OK" else "CAPTURE",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("ABORT MONITORING", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}
