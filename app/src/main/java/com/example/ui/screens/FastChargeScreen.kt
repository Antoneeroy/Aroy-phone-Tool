package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.core.privilege.AppVersionMode
import com.example.core.privilege.PrivilegeStatus
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald

@Composable
fun FastChargeScreen(
    privilegeStatus: PrivilegeStatus,
    onActivateFastCharge: () -> Unit,
    onOpenAirplaneSettings: () -> Unit,
    onOpenWifiSettings: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onOpenDisplaySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("fast_charge_screen_list")
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Fast Charge Accelerator",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Text(
                text = "Maximize charging speed by minimizing parasitic hardware draw",
                fontSize = 12.sp,
                color = glassColors.textSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Hero Accelerator Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                customBorderColor = NeonCyan.copy(alpha = 0.5f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Charge Boost",
                            tint = NeonCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Charge Speed Optimization",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )

                    Text(
                        text = if (privilegeStatus.activeAppMode == AppVersionMode.LITE)
                            "Lite Mode (Google Play Compliant): Quick one-tap system shortcuts to disable Airplane mode, GPS, Wi-Fi, and dim display."
                        else
                            "Pro Mode (Privileged): Automated radio suppression & background task freeze.",
                        fontSize = 12.sp,
                        color = glassColors.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onActivateFastCharge,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activate_fast_charge_button")
                    ) {
                        Text(
                            text = if (privilegeStatus.activeAppMode == AppVersionMode.LITE)
                                "Launch Fast Charge Assistance"
                            else
                                "Execute Automated Fast Charge Boost",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Hardware Radio Toggles / Shortcuts
        item {
            Text(
                text = "Hardware Power Leaks to Suppress",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            FastChargeActionCard(
                title = "Airplane Mode (Cellular Modems)",
                desc = "Cellular baseband consumes up to 300mA during active search.",
                icon = Icons.Default.AirplanemodeActive,
                actionLabel = "Airplane",
                onClick = onOpenAirplaneSettings
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            FastChargeActionCard(
                title = "Wi-Fi Scanning",
                desc = "Disabling Wi-Fi prevents continuous AP background beacon sweeps.",
                icon = Icons.Default.Wifi,
                actionLabel = "Wi-Fi",
                onClick = onOpenWifiSettings
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            FastChargeActionCard(
                title = "Bluetooth Radio",
                desc = "Turns off BLE advertising and peripheral polling.",
                icon = Icons.Default.Bluetooth,
                actionLabel = "Bluetooth",
                onClick = onOpenBluetoothSettings
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            FastChargeActionCard(
                title = "Screen Brightness & Refresh",
                desc = "AMOLED panel is the #1 consumer. Dimming drastically accelerates charge.",
                icon = Icons.Default.BrightnessLow,
                actionLabel = "Display",
                onClick = onOpenDisplaySettings
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FastChargeActionCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionLabel: String,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = desc,
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }
            }

            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(actionLabel, fontSize = 11.sp)
            }
        }
    }
}
