package com.seraphim.app.yxsg.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green80,
    onPrimaryContainer = Green20,
    inversePrimary = InversePrimaryLight,
    secondary = Orange40,
    onSecondary = Color.White,
    secondaryContainer = Orange80,
    onSecondaryContainer = Orange30,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue80,
    onTertiaryContainer = Color.Black,
    error = Error40,
    onError = Color.White,
    errorContainer = Error80,
    onErrorContainer = Color.Black,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = Color.Black,
    surfaceTint = SurfaceTintLight,
    background = Gray95,
    onBackground = Gray10,
    surface = Color.White,
    onSurface = Gray10,
    surfaceVariant = Gray90,
    onSurfaceVariant = Gray60,
    inverseSurface = Gray10,
    inverseOnSurface = Gray95,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green80,
    inversePrimary = InversePrimaryDark,
    secondary = Orange80,
    onSecondary = Orange30,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange80,
    tertiary = Blue80,
    onTertiary = Color.White,
    tertiaryContainer = Blue40,
    onTertiaryContainer = Color.Black,
    error = Error80,
    onError = Color.Black,
    errorContainer = Error40,
    onErrorContainer = Color.White,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = Color.Black,
    surfaceTint = SurfaceTintDark,
    background = Gray10,
    onBackground = Gray95,
    surface = Gray20,
    onSurface = Gray95,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray90,
    inverseSurface = Gray95,
    inverseOnSurface = Gray10,
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
)

@Composable
fun DelicaciesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
