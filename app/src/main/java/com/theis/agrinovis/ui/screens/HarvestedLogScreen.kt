package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
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
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.GrowthStageBadge
import com.theis.agrinovis.ui.theme.HarvestedGray
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.utils.PlantUtils
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import com.theis.agrinovis.visualization.threeD.LettuceScene
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HarvestedLogScreen(
    navController: NavController,
    viewModel: PlantsViewModel = viewModel()
) {
    val plants by viewModel.allPlants.collectAsState()
    val harvestedPlants = plants.filter { it.harvestStatus.equals("Harvested", ignoreCase = true) }
    
    var selectedPlant by remember { mutableStateOf<PlantEntity?>(null) }

    // Grouping logic: Map<DateString, List<PlantEntity>>
    val groupedRecords = remember(harvestedPlants) {
        harvestedPlants.groupBy { plant ->
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(plant.timestamp))
        }
    }

    AgrinovisScaffold(
        navController = navController,
        title = "Harvested Records"
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(DarkHologramBG).padding(padding)) {
            if (harvestedPlants.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.White.copy(alpha = 0.1f), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "NO HARVEST RECORDS",
                        color = Color.White.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    groupedRecords.forEach { (date, records) ->
                        item {
                            HistoryDateHeader(date)
                        }
                        items(records) { plant ->
                            HistoryListRow(plant) {
                                selectedPlant = plant
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }

            // Detail Modal
            selectedPlant?.let { plant ->
                HistoryDetailDialog(
                    plant = plant,
                    onDismiss = { selectedPlant = null }
                )
            }
        }
    }
}

@Composable
fun HistoryDateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = date.uppercase(),
            color = HarvestedGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun HistoryListRow(plant: PlantEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ID: ${plant.id}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${plant.variety.uppercase()} • ${plant.dap} DAP",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
        GrowthStageBadge(stage = "Harvested", modifier = Modifier.padding(end = 8.dp))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = HarvestedGray.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 24.dp),
        color = Color.White.copy(alpha = 0.05f)
    )
}

@Composable
fun HistoryDetailDialog(plant: PlantEntity, onDismiss: () -> Unit) {
    val harvestTime = remember(plant.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(plant.timestamp))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkHologramBG,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column {
                Text(
                    text = "ARCHIVE RETRIEVAL",
                    fontSize = 10.sp,
                    color = NeonGreen,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "PLANT ID: ${plant.id}",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 3D Model Preview based on variety and stage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    LettuceScene(
                        modifier = Modifier.fillMaxSize(),
                        modelPath = PlantUtils.getModelPathForVarietyAndStage(plant.variety, plant.stage),
                        scale = PlantUtils.getScaleForStage(plant.stage),
                        orbitRadius = PlantUtils.getCameraRadiusForStage(plant.stage),
                        showScanningText = false
                    )
                }

                // Identity
                DetailInfoItem("VARIETY", plant.variety.uppercase())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STAGE",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Black
                    )
                    GrowthStageBadge(stage = "Harvested")
                }
                DetailInfoItem("HARVEST TIME", harvestTime)
                
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                
                // Measurements
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DetailInfoItem("DIAMETER", "${plant.diameter} cm", modifier = Modifier.weight(1f))
                    DetailInfoItem("HEIGHT", "${plant.height} cm", modifier = Modifier.weight(1f))
                    DetailInfoItem("AGE", "${plant.dap} DAP", modifier = Modifier.weight(1f))
                }
                
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                
                // Action Log
                Column {
                    Text(text = "AUDIT TRAIL", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Successfully finalized harvest cycle via System Insights Dashboard manually.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE ARCHIVE", color = NeonGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DetailInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Black)
        Text(text = value, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
