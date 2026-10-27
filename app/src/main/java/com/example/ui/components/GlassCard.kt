package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlassColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    customBorderColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val glassColors = LocalGlassColors.current
    val borderColor = customBorderColor ?: glassColors.cardBorder

    val gradientBrush = if (glassColors.isDark) {
        Brush.verticalGradient(
            colors = listOf(
                glassColors.cardBackground.copy(alpha = 0.85f),
                glassColors.cardBackground.copy(alpha = 0.65f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                glassColors.cardBackground.copy(alpha = 0.95f),
                glassColors.cardBackground.copy(alpha = 0.82f)
            )
        )
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        border = BorderStroke(borderWidth, borderColor),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(16.dp),
            content = content
        )
    }
}
