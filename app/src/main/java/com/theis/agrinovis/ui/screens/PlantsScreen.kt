package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.GlassBackground
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.utils.PlantUtils
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import com.theis.agrinovis.visualization.threeD.LettuceScene

@Composable
fun PlantsScreen(
    navController: NavController,
    viewModel: PlantsViewModel = viewModel()
) {
    val plants by viewModel.allPlants.collectAsState()
    val activePlants = plants.filter { !it.harvestStatus.equals("Harvested", ignoreCase = true) }

    // Known growth stage order for logical chronological sorting
    val knownStageOrder = listOf(
        "SEEDLING",
        "VEGETATIVE",
        "DEVELOPING",
        "HEADING",
        "MATURITY",
        "READY TO HARVEST",
        "HARVEST READY"
    )

    // Dynamic grouping by growth stage from actual plant data
    val groupedPlants = activePlants
        .groupBy { it.stage.ifBlank { "UNSPECIFIED" }.uppercase() }
        .entries
        .sortedWith(compareBy { entry ->
            val index = knownStageOrder.indexOf(entry.key)
            if (index != -1) index else Int.MAX_VALUE
        })

    AgrinovisScaffold(
        navController = navController,
        title = "Monitoring Fleet"
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (activePlants.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NO MONITORED PLANTS",
                        color = Color.White.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Use the center '+' button to begin monitoring.",
                        color = Color.White.copy(alpha = 0.2f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedPlants.forEach { (stageName, plantList) ->
                        // Growth Stage Section Header (spans full width of 2 columns)
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                            key = "header_$stageName"
                        ) {
                            GrowthStageHeader(stage = stageName, count = plantList.size)
                        }

                        // Plant Cards for this growth stage section
                        items(
                            items = plantList,
                            key = { it.id }
                        ) { plant ->
                            PlantCard(plant) {
                                navController.navigate(Screen.PlantDetail.createRoute(plant.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GrowthStageHeader(stage: String, count: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(NeonGreen)
                )
                Text(
                    text = stage.uppercase(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 14.sp,
                    letterSpacing = 1.5.sp
                )
            }

            Surface(
                color = NeonGreen.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.25f))
            ) {
                Text(
                    text = "$count ${if (count == 1) "PLANT" else "PLANTS"}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 9.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = NeonGreen.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun PlantCard(plant: PlantEntity, onClick: () -> Unit) {
    val modelPath = PlantUtils.getModelPathForStage(plant.stage)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassBackground)
            .border(1.dp, NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.02f)),
                contentAlignment = Alignment.Center
            ) {
                LettuceScene(
                    modifier = Modifier.fillMaxSize(),
                    modelPath = modelPath,
                    scale = 1.4f,
                    showScanningText = false
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = plant.id, 
                    fontWeight = FontWeight.Black, 
                    color = NeonGreen, 
                    fontSize = 14.sp
                )
                Text(
                    text = plant.variety.uppercase(), 
                    fontSize = 11.sp, 
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = NeonGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = plant.stage.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 8.sp,
                            color = NeonGreen,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // SYNC STATUS BADGE
                    Surface(
                        color = if (plant.isSynced) NeonGreen.copy(alpha = 0.15f) else Color(0xFFFFB300).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (plant.isSynced) "☁️ SYNCED" else "🔄 PENDING",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 7.sp,
                            color = if (plant.isSynced) NeonGreen else Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Expected Harvest",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = plant.estHarvestDate.ifBlank { "N/A" },
                        fontSize = 11.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
