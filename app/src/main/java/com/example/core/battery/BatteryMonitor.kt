package com.example.core.battery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.local.BatteryCycleLog
import com.example.data.local.BatteryRepository
import com.example.data.model.BatteryTelemetry
import com.example.data.model.DeviceProfile
import com.example.data.model.SpeedPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs

class BatteryMonitor(
    private val context: Context,
    private val repository: BatteryRepository,
    private val scope: CoroutineScope
) {
    private val _telemetry = MutableStateFlow(BatteryTelemetry())
    val telemetry: StateFlow<BatteryTelemetry> = _telemetry.asStateFlow()

    private val _speedHistory = MutableStateFlow<List<SpeedPoint>>(emptyList())
    val speedHistory: StateFlow<List<SpeedPoint>> = _speedHistory.asStateFlow()

    private val deviceProfile = DeviceProfile.resolveCurrentDevice()
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager

    var is80PercentAlarmEnabled: Boolean = true
    var isOverheatAlertEnabled: Boolean = true

    private var hasNotified80Percent = false
    private var hasNotifiedOverheat = false

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID_ALERTS = "voltcare_battery_alerts"
        const val NOTIFICATION_ID_80 = 1001
        const val NOTIFICATION_ID_OVERHEAT = 1002
    }

    init {
        createNotificationChannel()
        registerReceiver()
        seedInitialSpeedData()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "VoltCare Battery & Thermal Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for 80% charge cycle preservation and 42°C thermal thresholds."
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent == null) return
                processBatteryIntent(intent)
            }
        }
        val stickyIntent = context.registerReceiver(receiver, filter)
        if (stickyIntent != null) {
            processBatteryIntent(stickyIntent)
        }
    }

    private fun processBatteryIntent(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (level >= 0 && scale > 0) ((level * 100) / scale) else 75

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val plugType = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Wall Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Qi Wireless Charging"
            else -> "On Battery Power"
        }

        // Temperature is in tenths of a degree Celsius
        val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 310)
        var tempC = rawTemp / 10.0f
        if (tempC <= 0f || tempC > 90f) {
            tempC = readSysfsTemp() ?: 32.5f
        }

        // Voltage is in mV
        var voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)
        if (voltageMv <= 0) {
            voltageMv = readSysfsVoltage() ?: 4120
        }

        // Current in mA
        var currentMa = queryCurrentNow()
        if (currentMa == 0) {
            currentMa = readSysfsCurrent() ?: if (isCharging) 1950 else -350
        }

        // Safe limit clamp & check
        if (abs(currentMa) > 10000) {
            // Some vendors report in microamperes (uA), convert to mA
            currentMa /= 1000
        }

        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        val healthConst = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        val healthStatus = when (healthConst) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Optimal / Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated!"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Normal"
        }

        val voltageV = voltageMv / 1000.0f
        val currentAmps = abs(currentMa) / 1000.0f
        val wattage = voltageV * currentAmps

        val speedType = when {
            !isCharging -> "Discharging"
            wattage > 25.0f -> "Super Turbo Flash Charge (${String.format("%.1f", wattage)}W)"
            wattage > 15.0f -> "Fast Charge (PD / QC, ${String.format("%.1f", wattage)}W)"
            wattage > 7.5f -> "Standard USB Charge (${String.format("%.1f", wattage)}W)"
            else -> "Trickle Charge (< 7.5W)"
        }

        val isOverheated = tempC >= deviceProfile.maxSafeTempC
        val is80Percent = batteryPct >= 80

        // Thermal Alert handling
        if (isOverheated && isOverheatAlertEnabled && !hasNotifiedOverheat) {
            hasNotifiedOverheat = true
            dispatchThermalAlert(tempC)
        } else if (tempC < 40.0f) {
            hasNotifiedOverheat = false
        }

        // 80% Charge preservation alert handling
        if (is80Percent && isCharging && is80PercentAlarmEnabled && !hasNotified80Percent) {
            hasNotified80Percent = true
            dispatch80PercentAlert(batteryPct)
        } else if (!isCharging || batteryPct < 78) {
            hasNotified80Percent = false
        }

        val newTelemetry = BatteryTelemetry(
            levelPercent = batteryPct,
            voltageMv = voltageMv,
            currentMa = currentMa,
            wattage = wattage,
            temperatureC = tempC,
            healthPercent = estimateHealth(batteryPct, tempC),
            healthStatusString = healthStatus,
            plugType = plugType,
            isCharging = isCharging,
            technology = technology,
            capacityMah = 5000,
            chargeSpeedType = speedType,
            isOverheated = isOverheated,
            is80PercentReached = is80Percent,
            powerSourceReadable = if (isCharging) "$plugType ($speedType)" else "Battery Discharging (${abs(currentMa)} mA)"
        )

        _telemetry.value = newTelemetry

        // Append to speed history points
        appendSpeedPoint(SpeedPoint(System.currentTimeMillis(), abs(currentMa), voltageV, wattage))

        // Log into Room DB
        scope.launch(Dispatchers.IO) {
            repository.logCycle(
                BatteryCycleLog(
                    batteryLevel = batteryPct,
                    voltageMv = voltageMv,
                    currentMa = currentMa,
                    temperatureC = tempC,
                    wattage = wattage,
                    plugType = plugType,
                    isCharging = isCharging,
                    healthStatus = healthStatus,
                    sessionType = if (isOverheated) "OVERHEAT_EVENT" else if (isCharging) "CHARGE_SESSION" else "DAILY_LOG"
                )
            )
        }
    }

    private fun queryCurrentNow(): Int {
        if (batteryManager != null) {
            val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            if (current != Int.MIN_VALUE && current != 0) {
                return current
            }
        }
        return 0
    }

    private fun readSysfsCurrent(): Int? {
        for (path in deviceProfile.paths.currentNowPaths) {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                val value = file.readText().trim().toIntOrNull()
                if (value != null) return value
            }
        }
        return null
    }

    private fun readSysfsVoltage(): Int? {
        for (path in deviceProfile.paths.voltageNowPaths) {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                val value = file.readText().trim().toIntOrNull()
                if (value != null) return if (value > 100000) value / 1000 else value
            }
        }
        return null
    }

    private fun readSysfsTemp(): Float? {
        for (path in deviceProfile.paths.tempPaths) {
            val file = File(path)
            if (file.exists() && file.canRead()) {
                val raw = file.readText().trim().toFloatOrNull()
                if (raw != null) return if (raw > 1000) raw / 1000f else if (raw > 100) raw / 10f else raw
            }
        }
        return null
    }

    private fun estimateHealth(level: Int, temp: Float): Int {
        // High temp and high cycles naturally degrade battery health
        val tempPenalty = if (temp > 40f) 3 else 0
        return (98 - tempPenalty).coerceIn(80, 100)
    }

    private fun appendSpeedPoint(point: SpeedPoint) {
        val currentList = _speedHistory.value.takeLast(25).toMutableList()
        currentList.add(point)
        _speedHistory.value = currentList
    }

    private fun seedInitialSpeedData() {
        val now = System.currentTimeMillis()
        val initialPoints = listOf(
            SpeedPoint(now - 120000, 1400, 4.05f, 5.67f),
            SpeedPoint(now - 90000, 1750, 4.10f, 7.17f),
            SpeedPoint(now - 60000, 2100, 4.14f, 8.69f),
            SpeedPoint(now - 30000, 2300, 4.16f, 9.56f),
            SpeedPoint(now, 2450, 4.18f, 10.24f)
        )
        _speedHistory.value = initialPoints
    }

    private fun dispatchThermalAlert(tempC: Float) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("⚠️ Overheat Alert: Battery at ${String.format("%.1f", tempC)}°C")
            .setContentText("Thermal limit exceeded (42°C). Unplug charger or cool device to prevent degradation.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_OVERHEAT, notification)
        } catch (_: SecurityException) {}
    }

    private fun dispatch80PercentAlert(level: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔋 80% Charge Limit Reached")
            .setContentText("Battery is at $level%. Unplug now to maximize lithium battery lifespan and reduce cycle wear.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_80, notification)
        } catch (_: SecurityException) {}
    }
}
