package com.theis.agrinovis.visualization.threeD

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import io.github.sceneview.SceneView
import io.github.sceneview.SurfaceType
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

@Composable
fun LettuceScene(
    modifier: Modifier = Modifier,
    modelPath: String = "models/lettuce.glb",
    scale: Float = 0.7f, // Adjusted to 70% of the placeholder units
    showScanningText: Boolean = true
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    
    // 1. Realistic Environment Lighting (IBL)
    // We use a null environment as a base and then the default light will provide basic IBL
    val environment = rememberEnvironment(environmentLoader, isOpaque = false)
    
    // 2. Directional "Sun" Light for depth and shadows
    val mainLightNode = rememberMainLightNode(engine) {
        position = Position(x = 1.0f, y = 2.0f, z = 1.0f)
        intensity = 50_000.0f // Bright enough to show leaf textures
    }

    val cameraManipulator = rememberCameraManipulator(orbitRadius = 3.5f)

    // 3D Rotation Animation Logic
    val infiniteTransition = rememberInfiniteTransition(label = "lettuceRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "yRotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraManipulator = cameraManipulator,
            environment = environment,
            mainLightNode = mainLightNode,
            isOpaque = false,
            surfaceType = SurfaceType.TextureSurface
        ) {
            val modelInstance = rememberModelInstance(modelLoader, modelPath)
            
            modelInstance?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    // Fix vertical orientation and apply rotation
                    rotation = Rotation(x = -90f, y = rotation, z = 0f),
                    scaleToUnits = scale, 
                    autoAnimate = true
                )
            }
        }
        
        // High-tech loading text
        if (showScanningText) {
            Text(
                text = "SCANNING 3D SPACE...",
                color = Color(0xFF00E676).copy(alpha = 0.3f),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
