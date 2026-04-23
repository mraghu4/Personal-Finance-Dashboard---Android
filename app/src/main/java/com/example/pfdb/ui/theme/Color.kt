package com.example.pfdb.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium Dark Mode Colors from Web App
val BgBase = Color(0xFF0F172A) // Slate 900
val BgSurface = Color(0xFF1E293B) // Slate 800
val BgSurfaceGlass = Color(0xB31E293B) // Slate 800 with 0.7 opacity
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8) // Slate 400

val AccentBlue = Color(0xFF3B82F6)
val AccentPurple = Color(0xFF8B5CF6)
val AccentGradient = Brush.linearGradient(
    colors = listOf(AccentBlue, AccentPurple)
)

val Success = Color(0xFF10B981)
val Danger = Color(0xFFEF4444)
val Warning = Color(0xFFF59E0B)

val BorderColor = Color(0x14FFFFFF) // 0.08 opacity white
val GlassBorder = Color(0x1AFFFFFF) // 0.1 opacity white
