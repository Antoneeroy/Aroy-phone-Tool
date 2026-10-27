package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScreenTimeMode
import com.example.ui.components.GlassCard
import com.example.ui.theme.LiquidGlassTheme
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose
import com.example.ui.theme.NeonViolet

@Composable
fun ScreenTimeScreen(
    screenTimeHours: Float,
    isReadModeActive: Boolean,
    readModeIntensity: Float,
    onToggleReadMode: (Boolean) -> Unit,
    onChangeReadModeIntensity: (Float) -> Unit,
    currentTheme: LiquidGlassTheme,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val currentMode = ScreenTimeMode.fromHours(screenTimeHours)

    val hoursInt = screenTimeHours.toInt()
    val minutesInt = ((screenTimeHours - hoursInt) * 60).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("screen_time_screen_list")
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Screen Time & Eye Wellness",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Text(
                text = "Ocular strain stages, Blue Light Read Filter & Display Care",
                fontSize = 12.sp,
                color = glassColors.textSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Active Status Card with Mode Theme
        item {
            val modeColor = Color(currentMode.colorHex)

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                customBorderColor = modeColor.copy(alpha = 0.6f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(modeColor.copy(alpha = 0.2f))
                                    .border(1.dp, modeColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getModeIcon(currentMode),
                                    contentDescription = currentMode.title,
                                    tint = modeColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = currentMode.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = modeColor
                                )
                                Text(
                                    text = currentMode.hindiTitle,
                                    fontSize = 12.sp,
                                    color = glassColors.textSecondary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${hoursInt}h ${minutesInt}m",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Today's Usage",
                                fontSize = 10.sp,
                                color = glassColors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentMode.eyeAdvice,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = glassColors.textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Read Mode (Blue Light Filter) Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Read Mode",
                                tint = NeonAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Read Mode (Blue Light Filter)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.textPrimary
                                )
                                Text(
                                    text = "Applies warm amber tint to relieve retinal fatigue",
                                    fontSize = 11.sp,
                                    color = glassColors.textSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isReadModeActive,
                            onCheckedChange = onToggleReadMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonAmber,
                                checkedTrackColor = NeonAmber.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_read_mode")
                        )
                    }

                    if (isReadModeActive) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Filter Warmth Intensity: ${(readModeIntensity * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = glassColors.textSecondary
                        )
                        Slider(
                            value = readModeIntensity,
                            onValueChange = onChangeReadModeIntensity,
                            valueRange = 0.1f..0.8f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonAmber,
                                activeTrackColor = NeonAmber
                            ),
                            modifier = Modifier.testTag("read_mode_intensity_slider")
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Liquid Glass Theme Switcher Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentTheme == LiquidGlassTheme.BLACK_DIAMOND) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentTheme == LiquidGlassTheme.BLACK_DIAMOND)
                                    "Black Liquid Glass (Diamond)"
                                else
                                    "White Liquid Glass",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Switch between Obsidian Diamond & Frosted White",
                                fontSize = 11.sp,
                                color = glassColors.textSecondary
                            )
                        }
                    }

                    Switch(
                        checked = currentTheme == LiquidGlassTheme.BLACK_DIAMOND,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("toggle_liquid_theme")
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // The 5 Screen Time Modes Spectrum Header
        item {
            Text(
                text = "Screen Time Health Spectrum",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Text(
                text = "5 Ocular Stages & Clinical Eye Strain Indicators",
                fontSize = 11.sp,
                color = glassColors.textSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(ScreenTimeMode.values().size) { idx ->
            val mode = ScreenTimeMode.values()[idx]
            val isCurrent = mode == currentMode
            ModeSpectrumCard(mode = mode, isCurrent = isCurrent)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ModeSpectrumCard(mode: ScreenTimeMode, isCurrent: Boolean) {
    val glassColors = LocalGlassColors.current
    val color = Color(mode.colorHex)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp,
        borderWidth = if (isCurrent) 2.dp else 1.dp,
        customBorderColor = if (isCurrent) color else null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getModeIcon(mode),
                        contentDescription = mode.title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = mode.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) color else glassColors.textPrimary
                        )
                        if (isCurrent) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    Text(
                        text = mode.description,
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }
            }

            Text(
                text = "<= ${mode.maxHours.toInt()}h",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

private fun getModeIcon(mode: ScreenTimeMode): ImageVector {
    return when (mode) {
        ScreenTimeMode.SAFE -> Icons.Default.CheckCircle
        ScreenTimeMode.NORMAL -> Icons.Default.Visibility
        ScreenTimeMode.DENGUE -> Icons.Default.Warning
        ScreenTimeMode.DANGEROUS -> Icons.Default.Dangerous
        ScreenTimeMode.END_EYES -> Icons.Default.VisibilityOff
    }
}
