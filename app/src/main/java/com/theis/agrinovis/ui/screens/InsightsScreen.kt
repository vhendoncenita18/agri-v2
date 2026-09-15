package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.theis.agrinovis.features.insights.InsightsData
import com.theis.agrinovis.features.insights.InsightsUiState
import com.theis.agrinovis.features.insights.InsightsViewModel
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.components.HolographicCard
import com.theis.agrinovis.ui.components.HolographicDetailItem
import com.theis.agrinovis.ui.components.ShimmerSkeletonItem
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.GlassBackground
import com.theis.agrinovis.ui.theme.NeonGreen

@Composable
fun InsightsScreen(
    navController: NavController,
    viewModel: InsightsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    AgrinovisScaffold(
        navController = navController,
        title = "SYSTEM INSIGHTS",
        showBottomBar = true,
        showTopBar = true
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkHologramBG)
        ) {
            when (val currentState = state) {
                is InsightsUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("COMPUTING ANALYTIC ARRAYS...", color = NeonGreen.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        ShimmerSkeletonItem(height = 100.dp)
                        ShimmerSkeletonItem(height = 160.dp)
                        ShimmerSkeletonItem(height = 200.dp)
                    }
                }
                is InsightsUiState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🥬 NO LETTUCE ASSETS FOUND", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Monitored distribution matrices will manifest as soon as the first crop profile is saved into the database records.",
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate(Screen.CaptureDiameter.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                        ) {
                            Text("LAUNCH CAMERA CAPTURE", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is InsightsUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("INSIGHTS SYNCHRONIZATION FAILURE", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(currentState.message, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.loadInsights() }) {
                            Text("RETRY CALCULATION Flow")
                        }
                    }
                }
                is InsightsUiState.Success -> {
                    InsightsContent(
                        data = currentState.data,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun InsightsContent(
    data: InsightsData,
    viewModel: InsightsViewModel,
    navController: NavController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showAllHarvests by remember { mutableStateOf(false) }
    
    val displayItems = if (showAllHarvests) {
        data.upcomingHarvests
    } else {
        data.upcomingHarvests.take(5)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ... (previous items 1-5)
        // 1. Overview Section
        item {
            Text("FLEET METRICS PROFILE", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OverviewCard("TOTAL", data.totalPlants.toString(), modifier = Modifier.weight(1f))
                OverviewCard("GROWING", data.healthyGrowingCount.toString(), modifier = Modifier.weight(1f))
                OverviewCard("READY", data.readyToHarvestCount.toString(), modifier = Modifier.weight(1f), isAlert = false)
            }
        }

        // 2. Growth Stage Distribution
        item {
            HolographicCard(title = "Growth Stage Allocation") {
                data.stageDistribution.forEach { (stage, count) ->
                    val ratio = if (data.totalPlants > 0) count.toFloat() / data.totalPlants else 0f
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stage, color = Color.White, fontSize = 13.sp)
                            Text("$count plants", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = NeonGreen,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        // 3. Variety Distribution
        item {
            HolographicCard(title = "Taxonomic Split") {
                data.varietyDistribution.forEach { (variety, count) ->
                    HolographicDetailItem(variety.uppercase(), "$count Monitored")
                }
            }
        }

        // 4. Size aggregates
        item {
            HolographicCard(title = "Biometric Summary") {
                HolographicDetailItem("AVG HEIGHT", String.format("%.1f CM", data.avgHeight))
                HolographicDetailItem("AVG DIAMETER", String.format("%.1f CM", data.avgDiameter))
            }
        }

        // 5. Environmental logs snapshot
        item {
            HolographicCard(title = "Atmospheric Parameters") {
                HolographicDetailItem("AVG TEMP", String.format("%.1f °C", data.avgTemp))
                HolographicDetailItem("AVG HUMIDITY", String.format("%.1f %%", data.avgHumidity))
                HolographicDetailItem("AVG WIND", String.format("%.1f M/S", data.avgWind))
                HolographicDetailItem("AVG PRESSURE", String.format("%.0f HPA", data.avgPressure))
                HolographicDetailItem("DOMINANT SKY", data.dominantCondition)
            }
        }

        // 5b. Ready to Harvest Section
        item {
            Text("READY TO HARVEST MATRICES", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }

        if (data.readyToHarvestPlants.isEmpty()) {
            item {
                Text(
                    "No crop assets currently qualified for harvest.",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(data.readyToHarvestPlants) { crop ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBackground)
                        .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ID: ${crop.id} (${crop.variety})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Age: ${crop.dap} DAP | Height: ${crop.height} cm", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                        Button(
                            onClick = { 
                                viewModel.harvestPlant(crop, context)
                                navController.navigate(Screen.HarvestedLog.route)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("HARVEST", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 6. Upcoming sorted harvests timeline
        item {
            Text("UPCOMING HARVEST SCHEDULE", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }

        if (data.upcomingHarvests.isEmpty()) {
            item {
                Text(
                    "No immediate mature cycles mapped.",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(displayItems) { crop ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBackground)
                        .border(1.dp, NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ID: ${crop.id} (${crop.variety})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Stage: ${crop.stage} | Est: ${crop.estHarvestDate}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (crop.daysToHarvest <= 3) Color.Red.copy(alpha = 0.2f) else NeonGreen.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${crop.daysToHarvest}d left",
                                color = if (crop.daysToHarvest <= 3) Color.Red else NeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
            
            if (data.upcomingHarvests.size > 5) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        IconButton(
                            onClick = { showAllHarvests = !showAllHarvests },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.1f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (showAllHarvests) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle List",
                                tint = NeonGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewCard(label: String, value: String, modifier: Modifier = Modifier, isAlert: Boolean = false) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBackground)
            .border(1.dp, (if (isAlert) Color.Red else NeonGreen).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = if (isAlert) Color.Red else NeonGreen, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}
