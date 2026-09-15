package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.theme.DarkHologramBG

@Composable
fun PredictionInputScreen(navController: NavController) {
    AgrinovisScaffold(
        navController = navController,
        title = "INPUT REFINEMENT",
        showBottomBar = false
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(DarkHologramBG)) {
            // Close Button in Upper Right
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("This section has been merged with Verification.", color = Color.White)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Return to Review")
                }
            }
        }
    }
}
