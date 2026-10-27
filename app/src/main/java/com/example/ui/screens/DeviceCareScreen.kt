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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.core.device.MemoryInfoData
import com.example.core.device.StorageInfoData
import com.example.core.privilege.AppVersionMode
import com.example.core.privilege.PrivilegeStatus
import com.example.data.model.AppUsageItem
import com.example.ui.components.GlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeviceCareScreen(
    memoryInfo: MemoryInfoData,
    storageInfo: StorageInfoData,
    unusedApps: List<AppUsageItem>,
    appDrains: List<AppUsageItem>,
    privilegeStatus: PrivilegeStatus,
    onCleanCacheClick: () -> Unit,
    onRequestUsageAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("device_care_screen_list")
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Device Care & Storage",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Text(
                text = "Hardware diagnostics, memory allocation & 6-month app scan",
                fontSize = 12.sp,
                color = glassColors.textSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // RAM Info Card
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
                                imageVector = Icons.Default.Memory,
                                contentDescription = "RAM",
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RAM Memory Allocation",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )
                        }
                        Text(
                            text = "${memoryInfo.usedPercent}% Used",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (memoryInfo.usedPercent > 85) NeonRose else NeonCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { memoryInfo.usedPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (memoryInfo.usedPercent > 85) NeonRose else NeonCyan,
                        trackColor = if (glassColors.isDark) Color(0x33334155) else Color(0x33CBD5E1)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Used: ${String.format("%.1f", memoryInfo.usedRamGb)} GB",
                            fontSize = 11.sp,
                            color = glassColors.textSecondary
                        )
                        Text(
                            text = "Free: ${String.format("%.1f", memoryInfo.freeRamGb)} GB",
                            fontSize = 11.sp,
                            color = NeonEmerald
                        )
                        Text(
                            text = "Total: ${String.format("%.1f", memoryInfo.totalRamGb)} GB",
                            fontSize = 11.sp,
                            color = glassColors.textPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // ROM Storage Card
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
                                imageVector = Icons.Default.SdStorage,
                                contentDescription = "ROM Storage",
                                tint = NeonEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Internal ROM Storage",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.textPrimary
                            )
                        }
                        Text(
                            text = "${storageInfo.usedPercent}% Used",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (storageInfo.usedPercent > 90) NeonRose else NeonEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { storageInfo.usedPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (storageInfo.usedPercent > 90) NeonRose else NeonEmerald,
                        trackColor = if (glassColors.isDark) Color(0x33334155) else Color(0x33CBD5E1)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Used: ${String.format("%.1f", storageInfo.usedRomGb)} GB",
                            fontSize = 11.sp,
                            color = glassColors.textSecondary
                        )
                        Text(
                            text = "Free: ${String.format("%.1f", storageInfo.freeRomGb)} GB",
                            fontSize = 11.sp,
                            color = NeonEmerald
                        )
                        Text(
                            text = "Total: ${String.format("%.1f", storageInfo.totalRomGb)} GB",
                            fontSize = 11.sp,
                            color = glassColors.textPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Transparent Cache Cleaner Card (Compliant with Play Store policies)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Cache Clean",
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (privilegeStatus.activeAppMode == AppVersionMode.LITE)
                                "Cache Cleanup (Play Store Lite)"
                            else
                                "Privileged Deep Cache Trimmer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (privilegeStatus.activeAppMode == AppVersionMode.LITE)
                            "Cleans VoltCare temporary cache files and opens Android system app storage settings. (No deceptive RAM cleaner claims)."
                        else
                            "Uses Root / Shizuku package manager commands to trim runtime Dalvik and app cache data across user packages.",
                        fontSize = 12.sp,
                        color = glassColors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCleanCacheClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clean_cache_button")
                    ) {
                        Text(
                            text = if (privilegeStatus.activeAppMode == AppVersionMode.LITE)
                                "Clean App Cache & Open Storage"
                            else
                                "Run Privileged Cache Trim",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Unused Apps (6 Months) Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Unused Apps (6+ Months)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = "Apps with zero usage in the last 180 days",
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }

                OutlinedButton(
                    onClick = onRequestUsageAccess,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("grant_usage_access_button")
                ) {
                    Text("Access", fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (unusedApps.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No apps unused for 6 months detected. Your device storage is actively rotated!",
                        fontSize = 12.sp,
                        color = glassColors.textSecondary
                    )
                }
            }
        } else {
            items(unusedApps) { app ->
                UnusedAppRow(app = app)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Per-App Battery Drain Breakdown Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Per-App Battery Drain Breakdown",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(appDrains) { drain ->
            AppDrainRow(item = drain)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UnusedAppRow(app: AppUsageItem) {
    val glassColors = LocalGlassColors.current
    val dateStr = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(app.lastUsedTimestamp))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
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
                        .background(NeonAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Unused",
                        tint = NeonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = app.appName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = "Last active: $dateStr",
                        fontSize = 11.sp,
                        color = glassColors.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonAmber.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "6+ Mos Unused",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonAmber
                )
            }
        }
    }
}

@Composable
private fun AppDrainRow(item: AppUsageItem) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.appName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = glassColors.textPrimary
                )
                LinearProgressIndicator(
                    progress = { (item.batteryDrainPercent / 30f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(0.85f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (item.batteryDrainPercent > 15f) NeonRose else NeonCyan,
                    trackColor = if (glassColors.isDark) Color(0x33334155) else Color(0x33CBD5E1)
                )
            }

            Text(
                text = "${item.batteryDrainPercent}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.batteryDrainPercent > 15f) NeonRose else NeonCyan
            )
        }
    }
}
