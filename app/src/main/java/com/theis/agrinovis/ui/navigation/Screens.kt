package com.theis.agrinovis.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Plants : Screen("plants")
    object AddPlant : Screen("add_plant")
    object CaptureDiameter : Screen("capture_diameter")
    object CaptureHeight : Screen("capture_height")
    object Verification : Screen("verification")
    object PredictionInput : Screen("prediction_input")
    object PredictionResult : Screen("prediction_result")
    object PlantDetail : Screen("plant_detail/{plantId}") {
        fun createRoute(plantId: String) = "plant_detail/$plantId"
    }
    object EditPlant : Screen("edit_plant/{plantId}") {
        fun createRoute(plantId: String) = "edit_plant/$plantId"
    }
    object HarvestedLog : Screen("harvested_log")
    object ActivityHistory : Screen("activity_history")
    object Information : Screen("information")
    object Insights : Screen("insights")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
}
