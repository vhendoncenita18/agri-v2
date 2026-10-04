package com.theis.agrinovis.visualization.threeD

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.utils.PlantUtils
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
    initialStage: String = "",
    scale: Float = 1.8f,
    orbitRadius: Float = 3.5f,
    showScanningText: Boolean = false,
    enableStageSelector: Boolean = false,
    enableVarietySelector: Boolean = false,
    initialVariety: String = "Olmetie",
    defaultRotationX: Float = 0f,
    defaultRotationY: Float = 0f,
    defaultRotationZ: Float = 0f,
    autoResetDelayMs: Long = 1500L,
    enableControls: Boolean = false,
    enableAutoReset: Boolean = true,
    onStageSelected: (String) -> Unit = {},
    onVarietySelected: (String) -> Unit = {}
) {
    var selectedVariety by remember(initialVariety) { mutableStateOf(initialVariety.ifBlank { "Olmetie" }) }
    val varieties = remember { listOf("Olmetie", "Limambo", "Orbital") }

    val activeModelPath = remember(modelPath, selectedVariety, enableVarietySelector, initialStage) {
        if (enableVarietySelector) {
            PlantUtils.getModelPathForVarietyAndStage(selectedVariety, initialStage.ifBlank { "vegetative" })
        } else modelPath
    }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    
    val environment = rememberEnvironment(environmentLoader, isOpaque = false)
    
    val mainLightNode = rememberMainLightNode(engine) {
        position = Position(x = 1.0f, y = 2.0f, z = 1.0f)
        intensity = 50_000.0f
    }

    val cameraManipulator = rememberCameraManipulator(orbitRadius = orbitRadius)

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
            val modelInstance = rememberModelInstance(modelLoader, activeModelPath)
            
            modelInstance?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    // Ensure standing upright position with bottom at the bottom for all models including heading
                    rotation = Rotation(x = defaultRotationX, y = rotation, z = defaultRotationZ),
                    scaleToUnits = scale, 
                    autoAnimate = true
                )
            }
        }
        
        // Interactive Variety Selector Overlay Bar at Top Center
        if (enableVarietySelector) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(50)),
                color = DarkHologramBG.copy(alpha = 0.85f),
                border = BorderStroke(0.5.dp, NeonGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    varieties.forEach { varietyName ->
                        val isSelected = varietyName.equals(selectedVariety, ignoreCase = true)
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(50)),
                            color = if (isSelected) NeonGreen.copy(alpha = 0.25f) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, NeonGreen) else null
                        ) {
                            Text(
                                text = varietyName.uppercase(),
                                color = if (isSelected) NeonGreen else Color.White.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier
                                    .clickable {
                                        selectedVariety = varietyName
                                        onVarietySelected(varietyName)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

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
