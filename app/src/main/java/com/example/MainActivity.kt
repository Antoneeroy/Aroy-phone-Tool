package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.privilege.AppVersionMode
import com.example.ui.components.AdMobBannerSimulation
import com.example.ui.components.DiamondPatternBackground
import com.example.ui.screens.BatteryScreen
import com.example.ui.screens.DeviceCareScreen
import com.example.ui.screens.FastChargeScreen
import com.example.ui.screens.InAppFeedbackDialog
import com.example.ui.screens.PowerModesScreen
import com.example.ui.screens.ScreenTimeScreen
import com.example.ui.screens.VipMonetizationSheet
import com.example.ui.theme.BlueLightFilterWarmTint
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRose
import com.example.ui.theme.VoltCareTheme
import com.example.viewmodel.VoltViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VoltViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            VoltCareTheme(liquidTheme = uiState.theme) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun MainAppScreen(viewModel: VoltViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val speedHistory by viewModel.speedHistory.collectAsStateWithLifecycle()
    val cycleLogs by viewModel.cycleLogs.collectAsStateWithLifecycle()
    val memoryInfo by viewModel.memoryInfo.collectAsStateWithLifecycle()
    val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
    val unusedApps by viewModel.unusedApps.collectAsStateWithLifecycle()
    val appDrains by viewModel.appDrains.collectAsStateWithLifecycle()
    val screenTimeHours by viewModel.screenTimeHours.collectAsStateWithLifecycle()

    val glassColors = LocalGlassColors.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userNoticeMessage) {
        uiState.userNoticeMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissNotice()
        }
    }

    val tabs = listOf(
        NavTabItem("Battery", Icons.Default.BatteryChargingFull, "nav_tab_battery"),
        NavTabItem("Care", Icons.Default.CleaningServices, "nav_tab_care"),
        NavTabItem("Screen Time", Icons.Default.Visibility, "nav_tab_screen_time"),
        NavTabItem("Power", Icons.Default.Speed, "nav_tab_power"),
        NavTabItem("Boost", Icons.Default.Bolt, "nav_tab_boost")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        DiamondPatternBackground {
            Scaffold(
                containerColor = Color.Transparent,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    VoltTopBar(
                        appMode = uiState.appMode,
                        isVip = uiState.isVip,
                        onToggleAppMode = { viewModel.toggleAppVersionMode() },
                        onVipClick = { viewModel.openVipSheet() },
                        onFeedbackClick = { viewModel.openFeedbackDialog() }
                    )
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        AdMobBannerSimulation(
                            isVip = uiState.isVip,
                            onUpgradeClick = { viewModel.openVipSheet() },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        VoltGlassBottomNav(
                            tabs = tabs,
                            selectedTab = uiState.currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (uiState.currentTab) {
                        0 -> BatteryScreen(
                            telemetry = telemetry,
                            speedHistory = speedHistory,
                            cycleLogs = cycleLogs,
                            is80PercentAlarmEnabled = uiState.is80PercentAlarmEnabled,
                            onToggle80PercentAlarm = { viewModel.toggle80PercentAlarm(it) },
                            isOverheatAlertEnabled = uiState.isOverheatAlertEnabled,
                            onToggleOverheatAlert = { viewModel.toggleOverheatAlert(it) },
                            onTriggerFastCharge = { viewModel.selectTab(4) },
                            onClearHistory = { viewModel.clearCycleHistory() }
                        )
                        1 -> DeviceCareScreen(
                            memoryInfo = memoryInfo,
                            storageInfo = storageInfo,
                            unusedApps = unusedApps,
                            appDrains = appDrains,
                            privilegeStatus = uiState.privilegeStatus,
                            onCleanCacheClick = { viewModel.performCacheClear() },
                            onRequestUsageAccess = { viewModel.requestUsageAccess() }
                        )
                        2 -> ScreenTimeScreen(
                            screenTimeHours = screenTimeHours,
                            isReadModeActive = uiState.isReadModeActive,
                            readModeIntensity = uiState.readModeIntensity,
                            onToggleReadMode = { viewModel.toggleReadMode(it) },
                            onChangeReadModeIntensity = { viewModel.setReadModeIntensity(it) },
                            currentTheme = uiState.theme,
                            onToggleTheme = { viewModel.toggleTheme() }
                        )
                        3 -> PowerModesScreen(
                            currentProfile = uiState.currentPowerProfile,
                            onSelectProfile = { viewModel.setPowerProfile(it) },
                            privilegeStatus = uiState.privilegeStatus,
                            onOpenBatterySaverSettings = { viewModel.openBatterySaverSettings() },
                            onToggleAppMode = { viewModel.toggleAppVersionMode() }
                        )
                        4 -> FastChargeScreen(
                            privilegeStatus = uiState.privilegeStatus,
                            onActivateFastCharge = { viewModel.triggerFastCharge() },
                            onOpenAirplaneSettings = { viewModel.openAirplaneSettings() },
                            onOpenWifiSettings = { viewModel.openWifiSettings() },
                            onOpenBluetoothSettings = { viewModel.openBluetoothSettings() },
                            onOpenDisplaySettings = { viewModel.openDisplaySettings() }
                        )
                    }
                }
            }
        }

        // Read Mode Warm Blue-Light Filter Simulation Overlay
        if (uiState.isReadModeActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF9E0B).copy(alpha = uiState.readModeIntensity))
            )
        }

        // VIP Monetization Bottom Sheet
        if (uiState.showVipSheet) {
            VipMonetizationSheet(
                activeAppMode = uiState.appMode,
                isVip = uiState.isVip,
                onPurchaseSuccess = { plan ->
                    viewModel.activateVip(plan)
                },
                onDismiss = { viewModel.closeVipSheet() }
            )
        }

        // In-App Feedback Dialog
        if (uiState.showFeedbackDialog) {
            InAppFeedbackDialog(
                onDismiss = { viewModel.closeFeedbackDialog() },
                onSubmitFeedback = { rating, cat, msg, specs ->
                    viewModel.submitFeedback(rating, cat, msg, specs)
                }
            )
        }
    }
}

@Composable
fun VoltTopBar(
    appMode: AppVersionMode,
    isVip: Boolean,
    onToggleAppMode: () -> Unit,
    onVipClick: () -> Unit,
    onFeedbackClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Logo",
                    tint = NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "VoltCare",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = glassColors.textPrimary
                )
                Text(
                    text = if (appMode == AppVersionMode.LITE) "Play Store Edition" else "Pro Direct APK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (appMode == AppVersionMode.LITE) NeonEmerald else NeonAmber
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mode Switcher pill (Lite vs Pro)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (appMode == AppVersionMode.LITE) NeonEmerald.copy(alpha = 0.15f) else NeonAmber.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (appMode == AppVersionMode.LITE) NeonEmerald.copy(alpha = 0.4f) else NeonAmber.copy(alpha = 0.4f),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onToggleAppMode() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("app_mode_toggle_badge")
            ) {
                Text(
                    text = if (appMode == AppVersionMode.LITE) "LITE" else "PRO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (appMode == AppVersionMode.LITE) NeonEmerald else NeonAmber
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // VIP Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isVip) NeonEmerald.copy(alpha = 0.2f) else NeonAmber.copy(alpha = 0.2f))
                    .clickable { onVipClick() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("vip_button_topbar")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "VIP",
                        tint = if (isVip) NeonEmerald else NeonAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isVip) "VIP ACTIVE" else "VIP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isVip) NeonEmerald else NeonAmber
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Feedback Button
            IconButton(
                onClick = onFeedbackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Feedback,
                    contentDescription = "Feedback",
                    tint = glassColors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun VoltGlassBottomNav(
    tabs: List<NavTabItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val glassColors = LocalGlassColors.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp)),
        color = glassColors.cardBackground.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.cardBorder),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag(tab.testTag)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) NeonCyan else glassColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonCyan else glassColors.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
