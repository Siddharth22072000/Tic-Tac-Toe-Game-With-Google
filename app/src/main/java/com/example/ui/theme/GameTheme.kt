package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val title: String,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val xColor: Color,
    val oColor: Color,
    val gridColor: Color,
    val primary: Color,
    val onBackground: Color,
    val onSurface: Color
) {
    CYBER_NEON(
        title = "Cyber Neon",
        isDark = true,
        background = Color(0xFF090D16),
        surface = Color(0xFF111827),
        surfaceVariant = Color(0xFF1F2937),
        xColor = Color(0xFF00E5FF),     // Electric Cyan
        oColor = Color(0xFFFF2E63),     // Neon Pink/Coral
        gridColor = Color(0xFF374151),
        primary = Color(0xFF6366F1),
        onBackground = Color(0xFFF3F4F6),
        onSurface = Color(0xFFE5E7EB)
    ),
    MIDNIGHT_GALAXY(
        title = "Galaxy Violet",
        isDark = true,
        background = Color(0xFF0A0915),
        surface = Color(0xFF141226),
        surfaceVariant = Color(0xFF221F3D),
        xColor = Color(0xFFA855F7),     // Electric Purple
        oColor = Color(0xFFF59E0B),     // Solar Gold
        gridColor = Color(0xFF3B3560),
        primary = Color(0xFF8B5CF6),
        onBackground = Color(0xFFF8FAFC),
        onSurface = Color(0xFFEDE9FE)
    ),
    MINIMAL_LIGHT(
        title = "Modern Light",
        isDark = false,
        background = Color(0xFFF8FAFC),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1F5F9),
        xColor = Color(0xFF2563EB),     // Royal Blue
        oColor = Color(0xFFDC2626),     // Crimson Red
        gridColor = Color(0xFFCBD5E1),
        primary = Color(0xFF0284C7),
        onBackground = Color(0xFF0F172A),
        onSurface = Color(0xFF1E293B)
    ),
    RETRO_ARCADE(
        title = "Retro Emerald",
        isDark = true,
        background = Color(0xFF0C140F),
        surface = Color(0xFF132219),
        surfaceVariant = Color(0xFF1B3325),
        xColor = Color(0xFF10B981),     // Emerald
        oColor = Color(0xFFF97316),     // Arcade Orange
        gridColor = Color(0xFF2D4D3A),
        primary = Color(0xFF059669),
        onBackground = Color(0xFFECFDF5),
        onSurface = Color(0xFFD1FAE5)
    )
}
