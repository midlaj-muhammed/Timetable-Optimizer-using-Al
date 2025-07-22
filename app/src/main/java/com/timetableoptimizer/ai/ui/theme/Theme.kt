package com.timetableoptimizer.ai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AppleBlue,
    onPrimary = White,
    primaryContainer = AppleBlueDark,
    onPrimaryContainer = AppleBlueLight,
    secondary = AppleCyan,
    onSecondary = White,
    secondaryContainer = AppleCyanDark,
    onSecondaryContainer = AppleCyanLight,
    tertiary = ApplePurple,
    onTertiary = White,
    tertiaryContainer = ApplePurpleDark,
    onTertiaryContainer = ApplePurpleLight,
    error = AppleRed,
    onError = White,
    errorContainer = AppleRedDark,
    onErrorContainer = AppleRedLight,
    background = Black,
    onBackground = White,
    surface = DarkGray,
    onSurface = White,
    surfaceVariant = DarkGrayVariant,
    onSurfaceVariant = LightGray,
    outline = MediumGray,
    outlineVariant = DarkGrayVariant,
    scrim = Black,
    inverseSurface = White,
    inverseOnSurface = Black,
    inversePrimary = AppleBlue,
    surfaceDim = DarkGray,
    surfaceBright = MediumGray,
    surfaceContainerLowest = Black,
    surfaceContainerLow = DarkGray,
    surfaceContainer = DarkGrayVariant,
    surfaceContainerHigh = MediumGray,
    surfaceContainerHighest = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = AppleBlue,
    onPrimary = White,
    primaryContainer = AppleBlueLight,
    onPrimaryContainer = AppleBlueDark,
    secondary = AppleCyan,
    onSecondary = White,
    secondaryContainer = AppleCyanLight,
    onSecondaryContainer = AppleCyanDark,
    tertiary = ApplePurple,
    onTertiary = White,
    tertiaryContainer = ApplePurpleLight,
    onTertiaryContainer = ApplePurpleDark,
    error = AppleRed,
    onError = White,
    errorContainer = AppleRedLight,
    onErrorContainer = AppleRedDark,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = LightGrayVariant,
    onSurfaceVariant = DarkGray,
    outline = MediumGray,
    outlineVariant = LightGray,
    scrim = Black,
    inverseSurface = DarkGray,
    inverseOnSurface = White,
    inversePrimary = AppleBlueLight,
    surfaceDim = LightGrayVariant,
    surfaceBright = White,
    surfaceContainerLowest = White,
    surfaceContainerLow = LightGrayVariant,
    surfaceContainer = LightGray,
    surfaceContainerHigh = MediumGray,
    surfaceContainerHighest = DarkGray
)

@Composable
fun TimetableOptimizerAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
