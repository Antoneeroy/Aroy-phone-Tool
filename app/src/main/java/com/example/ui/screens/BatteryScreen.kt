package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BatteryCycleLog
import com.example.data.model.BatteryTelemetry
import com.example.data.model.DeviceProfile
import com.example.data.model.SpeedPoint
import com.example.ui.components.CircularBatteryGauge
import com.example.ui.components.GlassCard
import com.example.ui.components.LiveChargingSpeedGraph
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BatteryScreen(
    telemetry: BatteryTelemetry,
    speedHistory: List<SpeedPoint>,
    cycleLogs: List<BatteryCycleLog>,
    is80PercentAlarmEnabled: Boolean,
    onToggle80PercentAlarm: (Boolean) -> Unit,
    isOverheatAlertEnabled: Boolean,
    onToggleOverheatAlert: (Boolean) -> Unit,
    onTriggerFastCharge: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val currentDeviceProfile = DeviceProfile.resolveCurrentDevice()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("battery_screen_list")
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            CircularBatteryGauge(telemetry = telemetry)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live Charging Speed Graph
        item {
            LiveChargingSpeedGraph(history = speedHistory)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Fast Charge Mode Banner
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTriggerFastCharge() }
                    .testTag("fast_charge_banner_card")
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Fast Charge",
                                tint = NeonCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Fast Charge Optimization",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Dim screen, disable radios & throttle background tasks",
                                fontSize = 11.sp,
                                color = glassColors.textSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onTriggerFastCharge,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("boost_charge_button")
                    ) {
                        Text(
                            text = "Boost",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Alarms & Safety Rules Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Battery Protection",
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Battery Health Protections",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 80% Charge Alarm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "80% Charge Lifespan Alarm",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Stops degradation. Prolongs Lithium-ion battery cycle health by 3x.",
                                fontSize = 11.sp,
                                color = glassColors.textSecondary
                            )
                        }
                        Switch(
                            checked = is80PercentAlarmEnabled,
                            onCheckedChange = onToggle80PercentAlarm,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_80_percent_alarm")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 42°C Overheat auto-alert
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "42°C Overheat Safety Warning",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = glassColors.textPrimary
                            )
                            Text(
                                text = "Alerts to unplug immediately when thermal threshold crosses 42°C.",
                                fontSize = 11.sp,
                                color = glassColors.textSecondary
                            )
                        }
                        Switch(
                            checked = isOverheatAlertEnabled,
                            onCheckedChange = onToggleOverheatAlert,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonRose,
                                checkedTrackColor = NeonRose.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_overheat_alert")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Device Profile Sysfs Tag
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (glassColors.isDark) Color(0x331E293B) else Color(0x1AE2E8F0))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Hardware Profile: ${currentDeviceProfile.brandName} • Safe Current Limit: ${currentDeviceProfile.safeMaxCurrentMa} mA",
                            fontSize = 11.sp,
                            color = glassColors.textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Daily Charge Cycle Logs Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Charge Cycle Logs (Room DB)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary
                )
                if (cycleLogs.isNotEmpty()) {
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Logs",
                            tint = glassColors.textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (cycleLogs.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No cycle logs recorded yet. Battery telemetry is logged automatically as your device charges.",
                        fontSize = 12.sp,
                        color = glassColors.textSecondary
                    )
                }
            }
        } else {
            items(cycleLogs.take(15)) { log ->
                CycleLogRow(log = log)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CycleLogRow(log: BatteryCycleLog) {
    val glassColors = LocalGlassColors.current
    val timeFormat = SimpleDateFormat("HH:mm:ss • dd MMM", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(log.timestamp))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (log.temperatureC >= 42f) NeonRose.copy(alpha = 0.2f)
                            else if (log.isCharging) NeonEmerald.copy(alpha = 0.2f)
                            else NeonCyan.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (log.isCharging) Icons.Default.Bolt else Icons.Default.Power,
                        contentDescription = "Status",
                        tint = if (log.temperatureC >= 42f) NeonRose else if (log.isCharging) NeonEmerald else NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "${log.batteryLevel}% • ${log.plugType}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.1f", log.wattage)} W (${log.currentMa} mA)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonCyan
                )
                Text(
                    text = "${String.format("%.1f", log.temperatureC)}°C • ${String.format("%.2f", log.voltageMv / 1000f)}V",
                    fontSize = 11.sp,
                    color = if (log.temperatureC >= 42f) NeonRose else glassColors.textSecondary
                )
            }
        }
    }
}
