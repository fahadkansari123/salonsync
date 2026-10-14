package com.salonsync.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WarmGold,
    secondary = SoftGold,
    tertiary = RoseGold,
    background = SlateDark,
    surface = SurfaceDark,
    onPrimary = SlateDark,
    onSecondary = SlateDark,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun SalonSyncTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
