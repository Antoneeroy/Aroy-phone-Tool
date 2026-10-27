package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BatteryTelemetry
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose

@Composable
fun CircularBatteryGauge(
    telemetry: BatteryTelemetry,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val animatedProgress by animateFloatAsState(
        targetValue = telemetry.levelPercent / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "BatteryLevelSweep"
    )

    val sweepGradient = Brush.sweepGradient(
        colors = listOf(
            NeonCyan,
            NeonEmerald,
            if (telemetry.isOverheated) NeonRose else NeonCyan
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("circular_battery_gauge"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val strokeWidth = 16.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(radius * 2, radius * 2)

                // Background track
                drawArc(
                    color = if (glassColors.isDark) Color(0x33334155) else Color(0x33CBD5E1),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active level arc
                drawArc(
                    brush = sweepGradient,
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Gauge Center Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (telemetry.isCharging) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Charging",
                            tint = NeonCyan,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "${telemetry.levelPercent}%",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = glassColors.textPrimary
                    )
                }

                Text(
                    text = telemetry.chargeSpeedType,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (telemetry.isOverheated) NeonRose else NeonEmerald,
                    maxLines = 1
                )

                if (telemetry.isOverheated) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Overheat Warning",
                            tint = NeonRose,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "42°C Exceeded",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonRose
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Key Telemetry Metrics Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricChip(
                label = "Temperature",
                value = "${String.format("%.1f", telemetry.temperatureC)}°C",
                highlightColor = if (telemetry.temperatureC >= 42f) NeonRose else if (telemetry.temperatureC >= 38f) NeonAmber else NeonEmerald
            )
            MetricChip(
                label = "Voltage",
                value = "${String.format("%.2f", telemetry.voltageVolts)} V",
                highlightColor = NeonCyan
            )
            MetricChip(
                label = "Current",
                value = "${telemetry.currentMa} mA",
                highlightColor = if (telemetry.currentMa > 0) NeonEmerald else NeonAmber
            )
            MetricChip(
                label = "Power",
                value = "${String.format("%.1f", telemetry.wattage)} W",
                highlightColor = NeonCyan
            )
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    highlightColor: Color
) {
    val glassColors = LocalGlassColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = glassColors.textSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
    }
}
