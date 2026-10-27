package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpeedPoint
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import kotlin.math.max

enum class GraphMetric(val label: String, val unit: String, val color: Color) {
    CURRENT("Current", "mA", NeonEmerald),
    WATTAGE("Power", "W", NeonCyan),
    VOLTAGE("Voltage", "V", NeonAmber)
}

@Composable
fun LiveChargingSpeedGraph(
    history: List<SpeedPoint>,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    var selectedMetric by remember { mutableStateOf(GraphMetric.WATTAGE) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("charging_speed_graph_card")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Charging Speed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = "Real-time telemetry stream",
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }

                // Metric Toggle Pills
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (glassColors.isDark) Color(0x331E293B) else Color(0x1AD1D5DB))
                        .padding(2.dp)
                ) {
                    GraphMetric.values().forEach { metric ->
                        val isSelected = metric == selectedMetric
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) metric.color.copy(alpha = 0.25f) else Color.Transparent
                                )
                                .clickable { selectedMetric = metric }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = metric.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) metric.color else glassColors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Values array extraction
            val points = if (history.isNotEmpty()) history else listOf(
                SpeedPoint(0, 1500, 4.1f, 6.15f),
                SpeedPoint(1, 1800, 4.15f, 7.47f),
                SpeedPoint(2, 2200, 4.2f, 9.24f)
            )

            val values = points.map { pt ->
                when (selectedMetric) {
                    GraphMetric.CURRENT -> pt.currentMa.toFloat()
                    GraphMetric.WATTAGE -> pt.wattage
                    GraphMetric.VOLTAGE -> pt.voltageV
                }
            }

            val maxVal = max(values.maxOrNull() ?: 1f, 0.1f)
            val minVal = values.minOrNull() ?: 0f
            val latestVal = values.lastOrNull() ?: 0f
            val avgVal = if (values.isNotEmpty()) values.average().toFloat() else 0f

            // Graph Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val w = size.width
                val h = size.height
                val paddingBottom = 16f
                val paddingTop = 12f
                val effectiveH = h - paddingBottom - paddingTop

                val range = max(maxVal - minVal, if (selectedMetric == GraphMetric.VOLTAGE) 0.5f else 2f)

                // Grid lines
                for (i in 0..3) {
                    val y = paddingTop + (effectiveH / 3f) * i
                    drawLine(
                        color = if (glassColors.isDark) Color(0x1AFFFFFF) else Color(0x1A000000),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                if (values.size >= 2) {
                    val stepX = w / (values.size - 1)
                    val linePath = Path()
                    val fillPath = Path()

                    values.forEachIndexed { index, v ->
                        val x = index * stepX
                        val normalized = ((v - minVal) / range).coerceIn(0f, 1f)
                        val y = (paddingTop + effectiveH * (1f - normalized))

                        if (index == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, h - paddingBottom)
                            fillPath.lineTo(x, y)
                        } else {
                            val prevX = (index - 1) * stepX
                            val prevNorm = ((values[index - 1] - minVal) / range).coerceIn(0f, 1f)
                            val prevY = (paddingTop + effectiveH * (1f - prevNorm))
                            val cx = (prevX + x) / 2f

                            linePath.cubicTo(cx, prevY, cx, y, x, y)
                            fillPath.cubicTo(cx, prevY, cx, y, x, y)
                        }
                    }

                    fillPath.lineTo(w, h - paddingBottom)
                    fillPath.close()

                    // Draw gradient under-fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                selectedMetric.color.copy(alpha = 0.35f),
                                selectedMetric.color.copy(alpha = 0.0f)
                            ),
                            startY = paddingTop,
                            endY = h - paddingBottom
                        )
                    )

                    // Draw glowing stroke line
                    drawPath(
                        path = linePath,
                        color = selectedMetric.color,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw indicator pulse dot on latest value
                    val lastX = (values.size - 1) * stepX
                    val lastNorm = ((latestVal - minVal) / range).coerceIn(0f, 1f)
                    val lastY = paddingTop + effectiveH * (1f - lastNorm)

                    drawCircle(
                        color = selectedMetric.color.copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = selectedMetric.color,
                        radius = 4.dp.toPx(),
                        center = Offset(lastX, lastY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GraphStatLabel("Live", "${formatMetricVal(latestVal, selectedMetric)} ${selectedMetric.unit}", selectedMetric.color)
                GraphStatLabel("Average", "${formatMetricVal(avgVal, selectedMetric)} ${selectedMetric.unit}", glassColors.textSecondary)
                GraphStatLabel("Peak", "${formatMetricVal(maxVal, selectedMetric)} ${selectedMetric.unit}", glassColors.textPrimary)
            }
        }
    }
}

private fun formatMetricVal(valNum: Float, metric: GraphMetric): String {
    return when (metric) {
        GraphMetric.CURRENT -> valNum.toInt().toString()
        GraphMetric.WATTAGE -> String.format("%.1f", valNum)
        GraphMetric.VOLTAGE -> String.format("%.2f", valNum)
    }
}

@Composable
private fun GraphStatLabel(title: String, value: String, color: Color) {
    Column {
        Text(text = title, fontSize = 10.sp, color = Color.Gray)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}
