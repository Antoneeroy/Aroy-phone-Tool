package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.privilege.AppVersionMode
import com.example.core.privilege.PrivilegeStatus
import com.example.core.privilege.PrivilegeTier
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose

enum class PowerProfile(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val cpuGovernor: String
) {
    POWER_SAVING(
        title = "Power Saving",
        description = "Limits background activity, caps CPU at 60%, enables dark display profile.",
        icon = Icons.Default.BatterySaver,
        accentColor = NeonEmerald,
        cpuGovernor = "powersave"
    ),
    BALANCED(
        title = "Balanced Mode",
        description = "Intelligent dynamic scaling. Balances day-to-day fluid performance with thermal efficiency.",
        icon = Icons.Default.BatteryChargingFull,
        accentColor = NeonCyan,
        cpuGovernor = "schedutil"
    ),
    HIGH_PERFORMANCE(
        title = "High Performance",
        description = "Unlocks maximum CPU frequency and 120Hz refresh rate. Higher thermal & battery draw.",
        icon = Icons.Default.Speed,
        accentColor = NeonRose,
        cpuGovernor = "performance"
    ),
    ULTRA_LOW(
        title = "Ultra Low / Emergency",
        description = "Extreme battery preservation. Minimal telemetry, monochrome mode guidance & radio throttle.",
        icon = Icons.Default.BatteryAlert,
        accentColor = NeonAmber,
        cpuGovernor = "conservative"
    )
}

@Composable
fun PowerModesScreen(
    currentProfile: PowerProfile,
    onSelectProfile: (PowerProfile) -> Unit,
    privilegeStatus: PrivilegeStatus,
    onOpenBatterySaverSettings: () -> Unit,
    onToggleAppMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("power_modes_screen_list")
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Power Profiles & Kernel Modes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Text(
                text = "Privilege auto-detection with graceful hardware fallback",
                fontSize = 12.sp,
                color = glassColors.textSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Privilege Detection Status Card
        item {
            val tierColor = Color(privilegeStatus.detectedTier.badgeColor)

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                customBorderColor = tierColor.copy(alpha = 0.5f)
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(tierColor.copy(alpha = 0.2f))
                                    .border(1.dp, tierColor, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Privilege Level",
                                    tint = tierColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = privilegeStatus.detectedTier.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tierColor
                                )
                                Text(
                                    text = "Active Build: ${privilegeStatus.activeAppMode.modeName}",
                                    fontSize = 11.sp,
                                    color = glassColors.textSecondary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onToggleAppMode,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("switch_version_mode_button")
                        ) {
                            Text(
                                text = if (privilegeStatus.activeAppMode == AppVersionMode.LITE) "Switch to Pro" else "Switch to Lite",
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (privilegeStatus.detectedTier == PrivilegeTier.ROOT)
                            "Root SuperUser verified. Direct kernel CPU governor sysfs nodes (/sys/devices/system/cpu/cpu*/cpufreq/scaling_governor) are actively controllable."
                        else if (privilegeStatus.detectedTier == PrivilegeTier.SHIZUKU)
                            "Shizuku ADB verified. System power policy and package standbys are controllable via remote shell."
                        else
                            "Standard Android sandbox (Lite Mode). Uses Google Play-compliant Battery Saver intents, display dimming & refresh rate guidance without simulated claims.",
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Power Profiles Selection Header
        item {
            Text(
                text = "Select Hardware Profile",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(PowerProfile.values().size) { idx ->
            val profile = PowerProfile.values()[idx]
            val isSelected = profile == currentProfile

            ProfileSelectionCard(
                profile = profile,
                isSelected = isSelected,
                privilegeStatus = privilegeStatus,
                onClick = { onSelectProfile(profile) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Android System Battery Saver Action Button (Non-Root Fallback)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "System Control",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Android System Battery Control",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Opens native Android Battery Saver settings to enable OS-level deep sleep and background radio management.",
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenBatterySaverSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_system_battery_saver_button")
                    ) {
                        Text(
                            text = "Open System Battery Saver",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileSelectionCard(
    profile: PowerProfile,
    isSelected: Boolean,
    privilegeStatus: PrivilegeStatus,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("profile_card_${profile.name.lowercase()}"),
        cornerRadius = 14.dp,
        borderWidth = if (isSelected) 2.dp else 1.dp,
        customBorderColor = if (isSelected) profile.accentColor else null
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
                        .clip(RoundedCornerShape(10.dp))
                        .background(profile.accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = profile.icon,
                        contentDescription = profile.title,
                        tint = profile.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) profile.accentColor else glassColors.textPrimary
                        )
                        if (privilegeStatus.detectedTier == PrivilegeTier.ROOT) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x33EF4444))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = profile.cpuGovernor,
                                    fontSize = 9.sp,
                                    color = NeonRose,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        text = profile.description,
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(profile.accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
