package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.battery.BatteryMonitor
import com.example.core.device.DeviceCareManager
import com.example.core.device.MemoryInfoData
import com.example.core.device.StorageInfoData
import com.example.core.privilege.AppVersionMode
import com.example.core.privilege.PrivilegeManager
import com.example.core.privilege.PrivilegeStatus
import com.example.data.local.AppDatabase
import com.example.data.local.BatteryCycleLog
import com.example.data.local.BatteryRepository
import com.example.data.model.AppUsageItem
import com.example.data.model.BatteryTelemetry
import com.example.data.model.SpeedPoint
import com.example.ui.screens.PowerProfile
import com.example.ui.screens.VipPlan
import com.example.ui.theme.LiquidGlassTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VoltUiState(
    val currentTab: Int = 0, // 0: Battery, 1: Care, 2: Screen Time, 3: Power, 4: Fast Charge
    val appMode: AppVersionMode = AppVersionMode.LITE,
    val privilegeStatus: PrivilegeStatus,
    val theme: LiquidGlassTheme = LiquidGlassTheme.BLACK_DIAMOND,
    val isReadModeActive: Boolean = false,
    val readModeIntensity: Float = 0.28f,
    val is80PercentAlarmEnabled: Boolean = true,
    val isOverheatAlertEnabled: Boolean = true,
    val isVip: Boolean = false,
    val activeVipPlan: VipPlan? = null,
    val currentPowerProfile: PowerProfile = PowerProfile.BALANCED,
    val userNoticeMessage: String? = null,
    val showVipSheet: Boolean = false,
    val showFeedbackDialog: Boolean = false
)

class VoltViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    private val database = AppDatabase.getInstance(context)
    private val repository = BatteryRepository(database.batteryDao())

    private val batteryMonitor = BatteryMonitor(context, repository, viewModelScope)
    private val deviceCareManager = DeviceCareManager(context)

    val telemetry: StateFlow<BatteryTelemetry> = batteryMonitor.telemetry
    val speedHistory: StateFlow<List<SpeedPoint>> = batteryMonitor.speedHistory
    val cycleLogs: StateFlow<List<BatteryCycleLog>> = repository.allLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val initialPrivilege = PrivilegeManager.inspectPrivileges(context, AppVersionMode.LITE)

    private val _uiState = MutableStateFlow(
        VoltUiState(
            privilegeStatus = initialPrivilege,
            is80PercentAlarmEnabled = batteryMonitor.is80PercentAlarmEnabled,
            isOverheatAlertEnabled = batteryMonitor.isOverheatAlertEnabled
        )
    )
    val uiState: StateFlow<VoltUiState> = _uiState.asStateFlow()

    private val _memoryInfo = MutableStateFlow(deviceCareManager.getMemoryInfo())
    val memoryInfo: StateFlow<MemoryInfoData> = _memoryInfo.asStateFlow()

    private val _storageInfo = MutableStateFlow(deviceCareManager.getStorageInfo())
    val storageInfo: StateFlow<StorageInfoData> = _storageInfo.asStateFlow()

    private val _unusedApps = MutableStateFlow(deviceCareManager.getUnusedApps6Months())
    val unusedApps: StateFlow<List<AppUsageItem>> = _unusedApps.asStateFlow()

    private val _appDrains = MutableStateFlow(deviceCareManager.getAppBatteryDrains())
    val appDrains: StateFlow<List<AppUsageItem>> = _appDrains.asStateFlow()

    private val _screenTimeHours = MutableStateFlow(deviceCareManager.getTodayScreenTimeHours())
    val screenTimeHours: StateFlow<Float> = _screenTimeHours.asStateFlow()

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(currentTab = index)
    }

    fun toggleAppVersionMode() {
        val newMode = if (_uiState.value.appMode == AppVersionMode.LITE) AppVersionMode.PRO else AppVersionMode.LITE
        val updatedPrivilege = PrivilegeManager.inspectPrivileges(context, newMode)
        _uiState.value = _uiState.value.copy(
            appMode = newMode,
            privilegeStatus = updatedPrivilege,
            userNoticeMessage = "Switched to ${newMode.modeName}"
        )
    }

    fun toggleTheme() {
        val newTheme = if (_uiState.value.theme == LiquidGlassTheme.BLACK_DIAMOND)
            LiquidGlassTheme.WHITE_LIQUID
        else
            LiquidGlassTheme.BLACK_DIAMOND
        _uiState.value = _uiState.value.copy(theme = newTheme)
    }

    fun toggleReadMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isReadModeActive = enabled)
    }

    fun setReadModeIntensity(intensity: Float) {
        _uiState.value = _uiState.value.copy(readModeIntensity = intensity)
    }

    fun toggle80PercentAlarm(enabled: Boolean) {
        batteryMonitor.is80PercentAlarmEnabled = enabled
        _uiState.value = _uiState.value.copy(
            is80PercentAlarmEnabled = enabled,
            userNoticeMessage = if (enabled) "80% charge lifespan alarm active" else "80% alarm disabled"
        )
    }

    fun toggleOverheatAlert(enabled: Boolean) {
        batteryMonitor.isOverheatAlertEnabled = enabled
        _uiState.value = _uiState.value.copy(
            isOverheatAlertEnabled = enabled,
            userNoticeMessage = if (enabled) "42°C thermal protection active" else "Overheat alert disabled"
        )
    }

    fun setPowerProfile(profile: PowerProfile) {
        _uiState.value = _uiState.value.copy(
            currentPowerProfile = profile,
            userNoticeMessage = "${profile.title} applied (Governor: ${profile.cpuGovernor})"
        )
    }

    fun performCacheClear() {
        PrivilegeManager.performCacheClear(
            context = context,
            privilegeStatus = _uiState.value.privilegeStatus,
            onSuccess = { msg ->
                _uiState.value = _uiState.value.copy(userNoticeMessage = msg)
                refreshDeviceCare()
            },
            onFallbackTriggered = { msg ->
                _uiState.value = _uiState.value.copy(userNoticeMessage = msg)
                refreshDeviceCare()
            }
        )
    }

    fun triggerFastCharge() {
        PrivilegeManager.triggerFastChargeAssistance(
            context = context,
            privilegeStatus = _uiState.value.privilegeStatus,
            onMessage = { msg ->
                _uiState.value = _uiState.value.copy(userNoticeMessage = msg)
            }
        )
    }

    fun openAirplaneSettings() {
        try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openDisplaySettings() {
        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openBatterySaverSettings() {
        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun requestUsageAccess() {
        deviceCareManager.openUsageAccessSettings()
    }

    fun clearCycleHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistory()
        }
        _uiState.value = _uiState.value.copy(userNoticeMessage = "Cycle history cleared")
    }

    fun openVipSheet() {
        _uiState.value = _uiState.value.copy(showVipSheet = true)
    }

    fun closeVipSheet() {
        _uiState.value = _uiState.value.copy(showVipSheet = false)
    }

    fun activateVip(plan: VipPlan) {
        _uiState.value = _uiState.value.copy(
            isVip = true,
            activeVipPlan = plan,
            showVipSheet = false,
            userNoticeMessage = "VIP Activated (${plan.period})! All Ads removed."
        )
    }

    fun openFeedbackDialog() {
        _uiState.value = _uiState.value.copy(showFeedbackDialog = true)
    }

    fun closeFeedbackDialog() {
        _uiState.value = _uiState.value.copy(showFeedbackDialog = false)
    }

    fun submitFeedback(rating: Int, category: String, message: String, attachSpecs: Boolean) {
        _uiState.value = _uiState.value.copy(
            userNoticeMessage = "Feedback submitted. Rating: $rating stars ($category)"
        )
    }

    fun dismissNotice() {
        _uiState.value = _uiState.value.copy(userNoticeMessage = null)
    }

    private fun refreshDeviceCare() {
        _memoryInfo.value = deviceCareManager.getMemoryInfo()
        _storageInfo.value = deviceCareManager.getStorageInfo()
        _screenTimeHours.value = deviceCareManager.getTodayScreenTimeHours()
    }
}
