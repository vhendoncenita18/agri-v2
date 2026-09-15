package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.components.futuristicTextFieldColors
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.components.futuristicTextFieldColors
import com.theis.agrinovis.ui.theme.NeonGreen

@Composable
fun AddPlantScreen(navController: NavController) {
    var plantId by remember { mutableStateOf("") }
    var variety by remember { mutableStateOf("") }
    var plantedDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AgrinovisScaffold(
        navController = navController,
        title = "Register New Plant",
        showBottomBar = false
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = plantId,
                onValueChange = { plantId = it },
                label = { Text("Plant ID (e.g., L001)", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                colors = futuristicTextFieldColors()
            )

            OutlinedTextField(
                value = variety,
                onValueChange = { variety = it },
                label = { Text("Variety", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                colors = futuristicTextFieldColors()
            )

            OutlinedTextField(
                value = plantedDate,
                onValueChange = { plantedDate = it },
                label = { Text("Planted Date", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                colors = futuristicTextFieldColors()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold),
                colors = futuristicTextFieldColors()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Save Plant", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}
