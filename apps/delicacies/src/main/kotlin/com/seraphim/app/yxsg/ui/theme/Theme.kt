package com.seraphim.app.yxsg.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Green80,
    onPrimaryContainer = Green20,
    secondary = Orange40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Orange80,
    onSecondaryContainer = Orange30,
    tertiary = Blue40,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Blue80,
    background = Gray95,
    onBackground = Gray10,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Gray10,
    surfaceVariant = Gray90,
    onSurfaceVariant = Gray60,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green80,
    secondary = Orange80,
    onSecondary = Orange30,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange80,
    tertiary = Blue80,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Blue40,
    background = Gray10,
    onBackground = Gray95,
    surface = Gray20,
    onSurface = Gray95,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray90,
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
        content = content,
    )
}
