package com.theis.agrinovis.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.theis.agrinovis.data.session.MonitoringSession
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.components.HolographicCard
import com.theis.agrinovis.ui.components.HolographicDetailItem
import com.theis.agrinovis.ui.components.ShimmerSkeletonItem
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.GlassBackground
import com.theis.agrinovis.ui.theme.GrowthStageBadge
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.utils.PlantUtils
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import com.theis.agrinovis.ui.viewmodels.SavePlantState
import com.theis.agrinovis.visualization.threeD.LettuceScene
import kotlinx.coroutines.delay

@Composable
fun PredictionResultScreen(
    navController: NavController,
    viewModel: PlantsViewModel = viewModel()
) {
    val plant = MonitoringSession.currentPlant
    var isLoading by remember { mutableStateOf(true) }
    var showSuccessCard by remember { mutableStateOf(false) }

    val saveState by viewModel.savePlantState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetSaveState()
        delay(2000) // Simulated neural processing delay
        isLoading = false
    }

    LaunchedEffect(saveState) {
        if (saveState is SavePlantState.Success) {
            showSuccessCard = true
        }
    }

    AgrinovisScaffold(
        navController = navController,
        title = "NEURAL ANALYSIS",
        showBottomBar = false
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkHologramBG)
        ) {
            if (plant == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("NEURAL DATA SYNC FAILED", color = NeonGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("RETRY MEASUREMENT")
                    }
                }
            } else {
                // Close Button
                IconButton(
                    onClick = { 
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { 
                                inclusive = true 
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                if (isLoading) {
                    // SKELETON LOADING VIEW
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                        Text("PROCESSING NEURAL DATA...", color = NeonGreen.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        ShimmerSkeletonItem(height = 200.dp)
                        ShimmerSkeletonItem(height = 150.dp)
                        ShimmerSkeletonItem(height = 56.dp)
                    }
                } else {
                    // MAIN CONTENT
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(top = 48.dp, bottom = 32.dp)
                    ) {
                        item {
                            Text(
                                text = "🌱 HARVEST ESTIMATION",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonGreen,
                                letterSpacing = 2.sp
                            )
                        }

                        // 3D Plant Representation Item
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(GlassBackground)
                                    .border(1.dp, NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                LettuceScene(
                                    modifier = Modifier.fillMaxSize(),
                                    modelPath = PlantUtils.getModelPathForStage(plant.stage),
                                    scale = PlantUtils.getScaleForStage(plant.stage),
                                    orbitRadius = PlantUtils.getCameraRadiusForStage(plant.stage),
                                    showScanningText = false
                                )
                            }
                        }

                        // ERROR ALERT BANNER FOR SUPABASE INSERT FAILURE
                        if (saveState is SavePlantState.Error) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFF5252).copy(alpha = 0.15f))
                                        .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Error,
                                            contentDescription = null,
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = (saveState as SavePlantState.Error).message,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            // Futuristic Result Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(GlassBackground)
                                    .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "DAYS TO HARVEST", color = Color.White, fontSize = 12.sp)
                                    Text(text = "${plant.daysToHarvest} DAYS", color = NeonGreen, fontSize = 48.sp, fontWeight = FontWeight.Black)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.1f))
                                    Text(text = "EXPECTED DATE", color = Color.White, fontSize = 11.sp)
                                    Text(text = plant.estHarvestDate, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        item {
                            HolographicCard(title = "Summary Data") {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "GROWTH STAGE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    GrowthStageBadge(stage = plant.stage)
                                }
                                HolographicDetailItem("LEAF COUNT", plant.leafCount.toString())
                                HolographicDetailItem("DIAMETER", "${plant.diameter} CM")
                                HolographicDetailItem("HEIGHT", "${plant.height} CM")
                            }
                        }

                        item {
                            Button(
                                onClick = { 
                                    viewModel.savePlant(plant)
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                enabled = saveState !is SavePlantState.Loading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonGreen,
                                    disabledContainerColor = NeonGreen.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                if (saveState is SavePlantState.Loading) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("COMMITTING TO SUPABASE...", fontWeight = FontWeight.Black, color = Color.Black)
                                } else {
                                    Text("COMMIT TO ARCHIVES", fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            // SUCCESS OVERLAY
            AnimatedVisibility(
                visible = showSuccessCard,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkHologramBG),
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier
                            .padding(32.dp)
                            .border(2.dp, NeonGreen, RoundedCornerShape(32.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(64.dp))
                            Text("PLANT ARCHIVED", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            Text("Neural records updated & saved to Supabase successfully.", color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { 
                                    viewModel.resetSaveState()
                                    navController.navigate(Screen.Plants.route) {
                                        popUpTo(Screen.Dashboard.route) { 
                                            inclusive = true 
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                            ) {
                                Text("VIEW FLEET", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
