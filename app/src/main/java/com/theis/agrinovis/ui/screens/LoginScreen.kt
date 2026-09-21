package com.theis.agrinovis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.theis.agrinovis.ui.components.futuristicTextFieldColors
import com.theis.agrinovis.ui.navigation.Screen
import com.theis.agrinovis.ui.theme.NeonGreen
import com.theis.agrinovis.ui.viewmodels.AuthState
import com.theis.agrinovis.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

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
                text = "LEAFUTURE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = NeonGreen,
                letterSpacing = 4.sp
            )
            Text(
                text = "SYSTEM AUTHENTICATION",
                fontSize = 12.sp,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("EMAIL_ADDRESS", color = Color.White) },
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
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = futuristicTextFieldColors()
            )

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("INITIALIZE", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            TextButton(
                onClick = { navController.navigate(Screen.Signup.route) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("NEW USER? CREATE PROFILE", color = NeonGreen.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }
    }
}
