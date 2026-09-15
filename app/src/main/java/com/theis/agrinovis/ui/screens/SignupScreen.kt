package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.theis.agrinovis.ui.components.futuristicTextFieldColors
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.NeonGreen

@Composable
fun SignupScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CREATE PROFILE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = NeonGreen,
                letterSpacing = 2.sp
            )
            Text(
                text = "REGISTER NEW FARMER ENTITY",
                fontSize = 11.sp,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("FULL_NAME", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = futuristicTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("FARMER_ID", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = futuristicTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("ACCESS_CODE", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = futuristicTextFieldColors()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text("REGISTER", fontWeight = FontWeight.Bold, color = Color.Black)
            }

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("ALREADY REGISTERED? LOGIN", color = NeonGreen.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }
    }
}
