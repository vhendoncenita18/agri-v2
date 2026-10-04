package com.theis.agrinovis.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.theis.agrinovis.data.local.entities.PlantEntity
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.components.futuristicTextFieldColors
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlantScreen(
    navController: NavController,
    plantId: String,
    viewModel: PlantsViewModel = viewModel()
) {
    val context = LocalContext.current
    var plant by remember { mutableStateOf<PlantEntity?>(null) }
    
    // Form States - Measurements
    var height by remember { mutableStateOf("") }
    var diameter by remember { mutableStateOf("") }
    
    // Form States - Identity
    val varieties = listOf("Olmetie", "Limambo", "Orbital")
    var varietyExpanded by remember { mutableStateOf(false) }
    var variety by remember { mutableStateOf(varieties[0]) }
    
    val stages = listOf("Seedling", "Vegetative", "Heading", "Maturity")
    var stageExpanded by remember { mutableStateOf(false) }
    var growthStage by remember { mutableStateOf(stages[0]) }

    val statusOptions = listOf("Growing", "Ready to Harvest", "Harvested")
    var statusExpanded by remember { mutableStateOf(false) }
    var harvestStatus by remember { mutableStateOf(statusOptions[0]) }

    var plantedDate by remember { mutableStateOf("") }
    var dap by remember { mutableStateOf("0") }
    var leafCount by remember { mutableStateOf("") }

    // Form States - Environmental
    var locationName by remember { mutableStateOf("") }
    var temp by remember { mutableStateOf("") }
    var humidity by remember { mutableStateOf("") }
    var windSpeed by remember { mutableStateOf("") }
    var pressure by remember { mutableStateOf("") }
    var rainfall by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }

    // Date Picker Logic
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            plantedDate = format.format(selectedDate.time)
            
            // Calculate DAP
            val diff = Date().time - selectedDate.timeInMillis
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            dap = days.coerceAtLeast(0).toString()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(plantId) {
        plant = viewModel.getPlantById(plantId)
        plant?.let {
            height = it.height.toString()
            diameter = it.diameter.toString()
            variety = it.variety
            growthStage = it.stage
            harvestStatus = it.harvestStatus
            plantedDate = it.plantedDate
            dap = it.dap.toString()
            leafCount = it.leafCount.toString()
            locationName = "" // Field doesn't exist in Entity yet? Let's check model...
            // Wait, PlantEntity has temp, humidity etc.
            temp = it.temp.toString()
            humidity = it.humidity.toString()
            windSpeed = it.windSpeed.toString()
            pressure = it.pressure.toString()
            rainfall = it.rainfall.toString()
            condition = it.condition
        }
    }

    // Function to construct updated plant
    fun getUpdatedPlant(): PlantEntity? {
        return plant?.copy(
            variety = variety,
            stage = growthStage,
            harvestStatus = if (harvestStatus == "Harvested") "Harvested" else if ((dap.toIntOrNull() ?: 0) >= 45) "Ready to Harvest" else "Growing",
            plantedDate = plantedDate,
            dap = dap.toIntOrNull() ?: 0,
            leafCount = leafCount.toIntOrNull() ?: 0,
            height = height.toDoubleOrNull() ?: 0.0,
            diameter = diameter.toDoubleOrNull() ?: 0.0,
            temp = temp.toDoubleOrNull() ?: 0.0,
            humidity = humidity.toIntOrNull() ?: 0,
            pressure = pressure.toIntOrNull() ?: 0,
            windSpeed = windSpeed.toDoubleOrNull() ?: 0.0,
            rainfall = rainfall.toDoubleOrNull() ?: 0.0,
            condition = condition
        )
    }

    AgrinovisScaffold(
        navController = navController,
        title = "EDIT RECORD: $plantId",
        showBottomBar = false,
        actions = {
            IconButton(onClick = {
                getUpdatedPlant()?.let {
                    viewModel.updatePlant(it)
                    navController.popBackStack()
                }
            }) {
                Icon(Icons.Default.Save, contentDescription = "Save", tint = NeonGreen)
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
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
                ) {
                    item {
                        Text(
                            text = "AI MEASUREMENT REVIEW",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen,
                            letterSpacing = 1.sp
                        )
                    }

                    // Measurement Fields
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = height,
                                onValueChange = { 
                                    if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                                        height = it 
                                    }
                                },
                                label = { Text("Plant Height (cm)", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = futuristicTextFieldColors()
                            )
                            OutlinedTextField(
                                value = diameter,
                                onValueChange = { 
                                    if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                                        diameter = it 
                                    }
                                },
                                label = { Text("Plant Diameter (cm)", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = futuristicTextFieldColors()
                            )
                        }
                    }

                    item { HorizontalDivider(color = Color.White.copy(alpha = 0.1f)) }

                    item {
                        Text(text = "PLANT IDENTITY", style = MaterialTheme.typography.labelMedium, color = NeonGreen)
                    }

                    item {
                        ExposedDropdownMenuBox(
                            expanded = varietyExpanded,
                            onExpandedChange = { varietyExpanded = !varietyExpanded },
                        ) {
                            OutlinedTextField(
                                value = variety,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Lettuce Variety", color = Color.White) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = varietyExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                colors = futuristicTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = varietyExpanded,
                                onDismissRequest = { varietyExpanded = false }
                            ) {
                                varieties.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text(text = v, color = Color.White.copy(alpha = 0.7f)) },
                                        onClick = {
                                            variety = v
                                            varietyExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = plantedDate,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Planted Date", color = Color.White) },
                                modifier = Modifier.weight(1.5f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                trailingIcon = {
                                    IconButton(onClick = { datePickerDialog.show() }) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, tint = NeonGreen)
                                    }
                                },
                                colors = futuristicTextFieldColors()
                            )
                            OutlinedTextField(
                                value = dap,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("DAP", color = Color.White) },
                                modifier = Modifier.weight(0.5f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                colors = futuristicTextFieldColors()
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = leafCount,
                            onValueChange = { 
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    leafCount = it 
                                }
                            },
                            label = { Text("Leaf Count", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = futuristicTextFieldColors()
                        )
                    }

                    item {
                        ExposedDropdownMenuBox(
                            expanded = stageExpanded,
                            onExpandedChange = { stageExpanded = !stageExpanded },
                        ) {
                            OutlinedTextField(
                                value = growthStage,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Growth Stage", color = Color.White) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                colors = futuristicTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = stageExpanded,
                                onDismissRequest = { stageExpanded = false }
                            ) {
                                stages.forEach { stage ->
                                    DropdownMenuItem(
                                        text = { Text(text = stage, color = Color.White.copy(alpha = 0.7f)) },
                                        onClick = {
                                            growthStage = stage
                                            stageExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Harvest Status selector removed as it is now automated by DAP threshold (>= 45 days)

                    item { HorizontalDivider(color = Color.White.copy(alpha = 0.1f)) }

                    item {
                        Text(text = "ENVIRONMENTAL PARAMETERS", style = MaterialTheme.typography.labelMedium, color = NeonGreen)
                    }

                    item {
                        OutlinedTextField(
                            value = locationName,
                            onValueChange = { locationName = it },
                            label = { Text("Location Name / City", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                            colors = futuristicTextFieldColors()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = temp,
                                onValueChange = { 
                                    if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".") || it == "-") {
                                        temp = it 
                                    }
                                },
                                label = { Text("Temp (°C)", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = futuristicTextFieldColors()
                            )
                            OutlinedTextField(
                                value = humidity,
                                onValueChange = { 
                                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                        humidity = it 
                                    }
                                },
                                label = { Text("Humidity (%)", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = futuristicTextFieldColors()
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = windSpeed,
                                onValueChange = { 
                                    if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                                        windSpeed = it 
                                    }
                                },
                                label = { Text("Wind (m/s)", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = futuristicTextFieldColors()
                            )
                            OutlinedTextField(
                                value = pressure,
                                onValueChange = { 
                                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                        pressure = it 
                                    }
                                },
                                label = { Text("hPa", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = futuristicTextFieldColors()
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = rainfall,
                                onValueChange = { 
                                    if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                                        rainfall = it 
                                    }
                                },
                                label = { Text("Rainfall (mm)", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = futuristicTextFieldColors()
                            )
                            OutlinedTextField(
                                value = condition,
                                onValueChange = { condition = it },
                                label = { Text("Condition", color = Color.White) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                                colors = futuristicTextFieldColors()
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                getUpdatedPlant()?.let {
                                    viewModel.updatePlant(it)
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
