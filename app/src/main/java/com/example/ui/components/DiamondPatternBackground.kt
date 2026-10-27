package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.LocalGlassColors

@Composable
fun DiamondPatternBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glassColors = LocalGlassColors.current
    val latticeColor = glassColors.diamondPatternColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(glassColors.background)
    ) {
        if (latticeColor != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 48f
                val strokeWidth = 1f
                val w = size.width
                val h = size.height

                // Draw diagonal lines in both directions to create a diamond pattern
                var x = -h
                while (x < w + h) {
                    drawLine(
                        color = latticeColor,
                        start = Offset(x, 0f),
                        end = Offset(x + h, h),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = latticeColor,
                        start = Offset(x, h),
                        end = Offset(x + h, 0f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    x += step
                }
            }
        }
        content()
    }
}
