package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.theis.agrinovis.data.model.NotificationModel
import com.theis.agrinovis.data.model.NotificationType
import com.theis.agrinovis.ui.components.AgrinovisScaffold
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.GlassBackground
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val notifications by homeViewModel.notifications.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.markNotificationsAsRead()
    }

    AgrinovisScaffold(
        navController = navController,
        title = "SYSTEM NOTIFICATIONS",
        showBottomBar = false
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkHologramBG)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "LIVE TELEMETRY LOGS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 2.sp
                )
            }

            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO TELEMETRY LOGS DETECTED",
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(notifications) { notification ->
                    NotificationItemCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(notification: NotificationModel) {
    val sdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val formattedTime = sdf.format(Date(notification.timestamp))

    val accentColor = when (notification.type) {
        NotificationType.HARVEST -> NeonGreen
        NotificationType.CLIMATE -> Color(0xFFFF3D00) // Vibrant warning Orange/Red
        NotificationType.SYSTEM -> Color(0xFF00E5FF) // Cyber Cyan
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (notification.isRead) GlassBackground else GlassBackground.copy(alpha = 0.3f))
            .border(
                width = if (notification.isRead) 1.dp else 2.dp,
                color = if (notification.isRead) accentColor.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notification.title,
                        color = accentColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            modifier = Modifier.size(6.dp),
                            shape = CircleShape,
                            color = Color(0xFFFF3D00)
                        ) {}
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notification.message,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Text(
                text = formattedTime,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
