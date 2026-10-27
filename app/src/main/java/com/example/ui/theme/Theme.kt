package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class LiquidGlassTheme {
    BLACK_DIAMOND,
    WHITE_LIQUID
}

data class GlassThemeColors(
    val background: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val primaryAccent: Color,
    val diamondPatternColor: Color?,
    val isDark: Boolean
)

val LocalGlassColors = staticCompositionLocalOf {
    GlassThemeColors(
        background = BlackGlassBg,
        cardBackground = BlackGlassCard,
        cardBorder = BlackGlassCardBorder,
        textPrimary = BlackGlassTextPrimary,
        textSecondary = BlackGlassTextSecondary,
        primaryAccent = NeonCyan,
        diamondPatternColor = BlackGlassDiamondLattice,
        isDark = true
    )
}

private val DarkMaterialScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonEmerald,
    tertiary = NeonAmber,
    background = BlackGlassBg,
    surface = BlackGlassCard,
    onPrimary = Color.Black,
    onBackground = BlackGlassTextPrimary,
    onSurface = BlackGlassTextPrimary
)

private val LightMaterialScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    secondary = Color(0xFF059669),
    tertiary = Color(0xFFD97706),
    background = WhiteGlassBg,
    surface = WhiteGlassCard,
    onPrimary = Color.White,
    onBackground = WhiteGlassTextPrimary,
    onSurface = WhiteGlassTextPrimary
)

@Composable
fun VoltCareTheme(
    liquidTheme: LiquidGlassTheme = LiquidGlassTheme.BLACK_DIAMOND,
    content: @Composable () -> Unit
) {
    val glassColors = when (liquidTheme) {
        LiquidGlassTheme.BLACK_DIAMOND -> GlassThemeColors(
            background = BlackGlassBg,
            cardBackground = BlackGlassCard,
            cardBorder = BlackGlassCardBorder,
            textPrimary = BlackGlassTextPrimary,
            textSecondary = BlackGlassTextSecondary,
            primaryAccent = NeonCyan,
            diamondPatternColor = BlackGlassDiamondLattice,
            isDark = true
        )
        LiquidGlassTheme.WHITE_LIQUID -> GlassThemeColors(
            background = WhiteGlassBg,
            cardBackground = WhiteGlassCard,
            cardBorder = WhiteGlassCardBorder,
            textPrimary = WhiteGlassTextPrimary,
            textSecondary = WhiteGlassTextSecondary,
            primaryAccent = Color(0xFF0284C7),
            diamondPatternColor = null,
            isDark = false
        )
    }

    val materialScheme = if (glassColors.isDark) DarkMaterialScheme else LightMaterialScheme

    CompositionLocalProvider(LocalGlassColors provides glassColors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = Typography,
            content = content
        )
    }
}
