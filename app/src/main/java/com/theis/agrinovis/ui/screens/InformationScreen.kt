package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.theis.agrinovis.ui.components.AgrinovisScaffold

@Composable
fun InformationScreen(navController: NavController) {
    AgrinovisScaffold(
        navController = navController,
        title = "Lettuce Information"
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Research & Growth Info",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                InfoSection(
                    title = "About Lettuce Monitoring",
                    content = "This system uses Computer Vision and Machine Learning to monitor the growth of lettuce plants. By capturing images regularly, we can estimate the time remaining until harvest."
                )
            }

            item {
                InfoSection(
                    title = "Thesis Objectives",
                    content = "[Space reserved for thesis-approved information regarding monitoring methodology, data collection, and prediction targets.]"
                )
            }

            item {
                InfoSection(
                    title = "Growing Stages",
                    content = "The system identifies stages such as Seedling, Vegetative, Heading, and Maturity. Each stage is mapped to a 3D visualization for better understanding."
                )
            }
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
