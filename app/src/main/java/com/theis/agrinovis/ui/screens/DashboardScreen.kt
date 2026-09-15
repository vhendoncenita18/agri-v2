package com.theis.agrinovis.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.theis.agrinovis.ui.components.*
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.LettuceBackground
import com.theis.agrinovis.ui.viewmodels.HomeViewModel
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    plantsViewModel: PlantsViewModel = viewModel()
) {
    val weather by homeViewModel.weatherState.collectAsState()
    val plants by plantsViewModel.allPlants.collectAsState()
    val unreadCount by homeViewModel.unreadCount.collectAsState()
    
    val activePlants = remember(plants) {
        plants.filter { !it.harvestStatus.equals("Harvested", ignoreCase = true) }
    }
    
    // Location Permission State
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Trigger weather fetch when permissions are granted
    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted) {
            homeViewModel.fetchWeatherByLocation()
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    // Growth Stages for cycling
    val stages = listOf("Seedling", "Vegetative", "Heading", "Maturity")
    var stageIndex by remember { mutableIntStateOf(0) }

    // Every 5 seconds, change the 3D model stage
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            stageIndex = (stageIndex + 1) % stages.size
        }
    }

    val currentStage = stages[stageIndex]

    // Notification Permission (Android 13+)
    val notificationPermissionState = rememberMultiplePermissionsState(
        permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
    )

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionState.launchMultiplePermissionRequest()
        }
    }

    // Monitoring for Harvest-Ready plants
    LaunchedEffect(plants) {
        val readyPlants = plants.filter { it.daysToHarvest <= 0 || it.stage.uppercase() == "MATURE" }
        readyPlants.forEach { plant ->
            // Use a unique check if possible, or trust addNotification check
            homeViewModel.addNotification(
                title = "HARVEST READY: PLANT ${plant.id}",
                message = "Neural analysis confirms optimal maturity for ${plant.variety}. Harvest sequence authorized.",
                type = com.theis.agrinovis.data.model.NotificationType.HARVEST,
                isSystemNotification = true
            )
        }
    }

    val currentDate = remember {
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    // Dialog state
    var selectedInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    val infoMapping = mapOf(
        "SEEDLING" to "The earliest stage after germination. Focus on root development and initial leaf emergence.",
        "EARLY GROWTH" to "Rapid expansion of leaves. Monitoring nutrient uptake is critical here.",
        "VEGETATIVE" to "Main growth phase where the lettuce gains most of its mass and volume.",
        "MATURE" to "Harvest-ready stage. Leaves are fully expanded and peak nutrition is achieved.",
        "GREEN CORAL" to "A loose-leaf variety known for its ruffled, curly leaves and mild, sweet flavor. It is highly heat-tolerant and matures quickly.",
        "PRIZEHEAD" to "Large, loose heads with reddish-bronze tinged leaves. Very crisp, slow to bolt, and ideal for home gardens.",
        "OLMETIE" to "A premium variety with thick, succulent leaves and excellent shelf life. It provides a satisfying crunch and sweet taste."
    )

    AgrinovisScaffold(
        navController = navController,
        title = "Dashboard",
        showTopBar = false // Custom header instead
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LettuceBackground)
                    .padding(padding)
            ) {
                item {
                    DashboardHeader(
                        farmerName = "Farmers", 
                        date = currentDate,
                        plantCount = activePlants.size,
                        unreadCount = unreadCount,
                        onNotificationClick = { navController.navigate(Screen.Notifications.route) }
                    )
                }
                
                // 3D Preview - Animated Evolution
                item {
                    ThreeDLettucePreview(stage = currentStage)
                }

                // Weather Card - Dynamic from API based on Real Location
                item {
                    WeatherCard(weather = weather)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // NEW: Lettuce Variety Section (Moved above Growth Stage)
                item {
                    LettuceVarietyCategorySection { item ->
                        selectedInfo = item to (infoMapping[item] ?: "No description available.")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    PlantCategorySection(
                        title = "Growth Stages",
                        items = listOf("SEEDLING", "EARLY GROWTH", "VEGETATIVE", "MATURE"),
                        onItemClick = { item ->
                            selectedInfo = item to (infoMapping[item] ?: "No description available.")
                        }
                    )
                }

                // Harvest Predictions - Dynamic from Fleet Records
                item {
                    HarvestPredictionsSummary(plants = activePlants)
                }
            }

            // Info Dialog overlay
            selectedInfo?.let { (title, description) ->
                DashboardInfoDialog(
                    title = title,
                    description = description,
                    onDismiss = { selectedInfo = null }
                )
            }
        }
    }
}
