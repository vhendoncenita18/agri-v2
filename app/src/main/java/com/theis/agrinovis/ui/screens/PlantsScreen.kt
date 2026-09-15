package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
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
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activePlants) { plant ->
                        PlantCard(plant) {
                            navController.navigate(Screen.PlantDetail.createRoute(plant.id))
                        }
                    }
                }
            }
        }
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
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "EST: ${plant.estHarvestDate}", 
                    fontSize = 10.sp, 
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
