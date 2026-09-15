package com.theis.agrinovis.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.DarkHologramBG
import com.theis.agrinovis.ui.theme.NavBarBackground
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.theme.SelectionPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgrinovisScaffold(
    navController: NavController,
    title: String,
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        containerColor = DarkHologramBG,
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                    navigationIcon = {
                        // Show back button if we are not on a top-level destination
                        val topLevelRoutes = listOf(Screen.Dashboard.route, Screen.Plants.route, Screen.Insights.route, Screen.Profile.route)
                        if (currentRoute !in topLevelRoutes && navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    },
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkHologramBG,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                AgrinovisBottomBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun AgrinovisBottomBar(navController: NavController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem("Home", Screen.Dashboard, Icons.Default.Home),
        BottomNavItem("Monitor", Screen.Plants, Icons.Default.Spa),
        // Placeholder for middle item
        BottomNavItem("", Screen.CaptureDiameter, Icons.Default.Add), 
        BottomNavItem("Insights", Screen.Insights, Icons.Default.BarChart),
        BottomNavItem("Profile", Screen.Profile, Icons.Default.Person)
    )

    Box(contentAlignment = Alignment.TopCenter) {
        NavigationBar(
            containerColor = NavBarBackground,
            contentColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier
                .height(80.dp)
                .selectableGroup()
        ) {
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    // Empty space for the floating action button
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Spacer(modifier = Modifier.size(24.dp)) },
                        label = null,
                        enabled = false
                    )
                } else {
                    val isSelected = currentRoute == item.screen.route
                    val onNavigate = {
                        if (currentRoute != item.screen.route) {
                            navController.navigate(item.screen.route) {
                                if (item.screen == Screen.Dashboard) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                                } else {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    restoreState = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = onNavigate,
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.label == "Alerts") {
                                        Badge(containerColor = Color.Red) {
                                            Text("1", color = Color.White, fontSize = 7.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (isSelected) NeonGreen else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                    indication = null,
                                    onClick = onNavigate
                                )
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Elevated Plus Button
        FloatingActionButton(
            onClick = { navController.navigate(Screen.CaptureDiameter.route) },
            containerColor = NeonGreen,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .offset(y = (-24).dp) // Move it higher
                .size(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Capture")
        }
    }
}

data class BottomNavItem(
    val label: String,
    val screen: Screen,
    val icon: ImageVector
)
