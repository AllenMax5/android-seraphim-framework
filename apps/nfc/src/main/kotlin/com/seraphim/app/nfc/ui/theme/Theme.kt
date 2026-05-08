package com.seraphim.app.nfc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = KeyWorldPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = KeyWorldPrimary.copy(alpha = 0.15f),
    onPrimaryContainer = KeyWorldPrimary,
    secondary = KeyWorldSecondary,
    onSecondary = Color.White,
    secondaryContainer = KeyWorldSecondary.copy(alpha = 0.15f),
    onSecondaryContainer = KeyWorldSecondary,
    tertiary = KeyWorldTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = KeyWorldTertiary.copy(alpha = 0.15f),
    onTertiaryContainer = KeyWorldTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = ErrorColor,
    onError = Color.White,
    outline = DarkOnSurfaceVariant.copy(alpha = 0.5f),
    outlineVariant = DarkSurfaceVariant,
    scrim = Color.Black,
    inverseSurface = LightSurface,
    inverseOnSurface = LightOnSurface,
    inversePrimary = KeyWorldPrimary.copy(alpha = 0.8f),
    surfaceTint = KeyWorldPrimary,
)

private val LightColorScheme = lightColorScheme(
    primary = KeyWorldPrimary,
    onPrimary = Color.White,
    primaryContainer = KeyWorldPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = KeyWorldPrimary.copy(alpha = 0.8f),
    secondary = KeyWorldSecondary,
    onSecondary = Color.White,
    secondaryContainer = KeyWorldSecondary.copy(alpha = 0.12f),
    onSecondaryContainer = KeyWorldSecondary,
    tertiary = KeyWorldTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = KeyWorldTertiary.copy(alpha = 0.12f),
    onTertiaryContainer = KeyWorldTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF616161),
    error = ErrorColor,
    onError = Color.White,
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
    scrim = Color.Black,
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = KeyWorldPrimary,
    surfaceTint = KeyWorldPrimary,
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
)

@Composable
fun KeyWorldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
