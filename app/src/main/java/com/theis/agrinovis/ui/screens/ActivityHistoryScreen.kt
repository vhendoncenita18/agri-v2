package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.theis.agrinovis.data.local.entities.ActivityLogEntity
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.viewmodels.PlantsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivityHistoryScreen(
    navController: NavController,
    viewModel: PlantsViewModel = viewModel()
) {
    val activities by viewModel.allActivities.collectAsState()

    val groupedActivities = remember(activities) {
        activities.groupBy { activity ->
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(activity.timestamp))
        }
    }

    AgrinovisScaffold(
        navController = navController,
        title = "Activity History"
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(DarkHologramBG).padding(padding)) {
            if (activities.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.White.copy(alpha = 0.1f), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "NO ACTIVITIES LOGGED",
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
                    groupedActivities.forEach { (date, logs) ->
                        item {
                            ActivityDateHeader(date)
                        }
                        items(logs) { log ->
                            ActivityLogItem(log)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityDateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = date.uppercase(),
            color = NeonGreen.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ActivityLogItem(log: ActivityLogEntity) {
    val time = remember(log.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = log.action,
                color = when (log.action) {
                    "REGISTERED" -> NeonGreen
                    "UPDATED" -> Color.Cyan
                    "DELETED" -> Color.Red
                    "HARVESTED" -> Color(0xFFFFA500)
                    else -> Color.White
                },
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = time,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = log.affectedRecord,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Performed by ${log.userName}",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
    }
}
