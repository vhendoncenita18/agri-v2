package com.theis.agrinovis.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    secondary = GreenGrey80,
    onSecondary = Color.White,
    tertiary = Lime80,
    onTertiary = Color.White,
    background = DarkHologramBG,
    onBackground = Color.White,
    surface = DarkHologramBG,
    onSurface = Color.White,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White,
    onTertiaryContainer = Color.White
)

private val LightColorScheme = darkColorScheme( // Forcing dark theme for futuristic look
    primary = NeonGreen,
    onPrimary = Color.Black,
    secondary = GreenGrey80,
    onSecondary = Color.White,
    tertiary = Lime80,
    onTertiary = Color.White,
    background = DarkHologramBG,
    onBackground = Color.White,
    surface = DarkHologramBG,
    onSurface = Color.White,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White,
    onTertiaryContainer = Color.White
)

@Composable
fun AgrinovisTheme(
    darkTheme: Boolean = true, // Force dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
