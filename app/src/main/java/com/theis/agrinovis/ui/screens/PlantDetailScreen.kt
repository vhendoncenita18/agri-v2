package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.components.HolographicCard
import com.theis.agrinovis.ui.components.HolographicDetailItem
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.utils.PlantUtils
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import com.theis.agrinovis.visualization.threeD.LettuceScene

@Composable
fun PlantDetailScreen(
    navController: NavController, 
    plantId: String,
    viewModel: PlantsViewModel = viewModel()
) {
    var plant by remember { mutableStateOf<PlantEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(plantId) {
        plant = viewModel.getPlantById(plantId)
    }

    if (showDeleteDialog && plant != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Purge Data?", color = Color.White) },
            text = { Text("Permanently delete this plant's neural record?", color = Color.White.copy(alpha = 0.7f)) },
            containerColor = DarkHologramBG,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlant(plant!!)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("DELETE", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }

    AgrinovisScaffold(
        navController = navController,
        title = "DATA CORE: ${plant?.id ?: "..."}",
        showBottomBar = false,
        actions = {
            if (plant != null) {
                IconButton(onClick = { navController.navigate(Screen.EditPlant.createRoute(plant!!.id)) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeonGreen)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(DarkHologramBG)) {
            if (plant == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        // 3D Header - Now with stage consistency
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.Black.copy(alpha = 0.2f))
                        ) {
                            LettuceScene(
                                modifier = Modifier.fillMaxSize(),
                                modelPath = PlantUtils.getModelPathForStage(plant!!.stage),
                                showScanningText = false
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            
                            HolographicCard(title = "Plant Information") {
                                HolographicDetailItem("VARIETY", plant!!.variety)
                                HolographicDetailItem("PLANTED ON", plant!!.plantedDate)
                                HolographicDetailItem("GROWTH AGE", "${plant!!.dap} DAYS")
                                HolographicDetailItem("LEAF COUNT", plant!!.leafCount.toString())
                            }

                            HolographicCard(title = "Atmospheric conditions") {
                                HolographicDetailItem("TEMPERATURE", "${plant!!.temp}°C")
                                HolographicDetailItem("HUMIDITY", "${plant!!.humidity}%")
                                HolographicDetailItem("AIR PRESSURE", "${plant!!.pressure} hPa")
                                HolographicDetailItem("WEATHER", plant!!.condition)
                            }

                            HolographicCard(title = "Neural Prediction") {
                                HolographicDetailItem("EST. HARVEST", plant!!.estHarvestDate)
                                HolographicDetailItem("TIME REMAINING", "${plant!!.daysToHarvest} DAYS")
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { 1f - (plant!!.daysToHarvest.toFloat() / 30f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = NeonGreen,
                                    trackColor = Color.White.copy(alpha = 0.1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
