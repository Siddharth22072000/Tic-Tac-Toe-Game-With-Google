package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalAppTheme = staticCompositionLocalOf { AppTheme.CYBER_NEON }

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.CYBER_NEON,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        appTheme.isDark -> darkColorScheme(
            primary = appTheme.primary,
            background = appTheme.background,
            surface = appTheme.surface,
            surfaceVariant = appTheme.surfaceVariant,
            onBackground = appTheme.onBackground,
            onSurface = appTheme.onSurface
        )
        else -> lightColorScheme(
            primary = appTheme.primary,
            background = appTheme.background,
            surface = appTheme.surface,
            surfaceVariant = appTheme.surfaceVariant,
            onBackground = appTheme.onBackground,
            onSurface = appTheme.onSurface
        )
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
