package com.example.data.model

data class BatteryTelemetry(
    val levelPercent: Int = 85,
    val voltageMv: Int = 4120,
    val currentMa: Int = 1850,
    val wattage: Float = 7.62f,
    val temperatureC: Float = 32.4f,
    val healthPercent: Int = 96,
    val healthStatusString: String = "Good",
    val plugType: String = "AC Charger",
    val isCharging: Boolean = true,
    val technology: String = "Li-ion",
    val capacityMah: Int = 5000,
    val chargeSpeedType: String = "Fast Charging (PD/PPS)", // Slow, Normal, Fast, SuperVOOC/Turbo
    val isOverheated: Boolean = false,
    val is80PercentReached: Boolean = false,
    val powerSourceReadable: String = "Plugged into AC (Fast)"
) {
    val voltageVolts: Float get() = voltageMv / 1000f
    val isCurrentSafe: Boolean get() = currentMa <= 6500 && temperatureC < 42.0f
}

data class SpeedPoint(
    val timestamp: Long,
    val currentMa: Int,
    val voltageV: Float,
    val wattage: Float
)

enum class ScreenTimeMode(
    val title: String,
    val hindiTitle: String,
    val maxHours: Float,
    val description: String,
    val colorHex: Long,
    val badgeIconName: String,
    val eyeAdvice: String
) {
    SAFE(
        title = "Safe Mode",
        hindiTitle = "सुरक्षित स्क्रीन समय",
        maxHours = 2.0f,
        description = "Under 2 hours daily. Retinal cells relaxed, minimal strain.",
        colorHex = 0xFF10B981, // Emerald
        badgeIconName = "EyeCheck",
        eyeAdvice = "Excellent ocular health! Keep 20-20-20 balance."
    ),
    NORMAL(
        title = "Normal Mode",
        hindiTitle = "संतुलित उपयोग",
        maxHours = 4.0f,
        description = "2 to 4 hours. Steady productivity with standard exposure.",
        colorHex = 0xFF00F2FE, // Cyan
        badgeIconName = "Laptop",
        eyeAdvice = "Blink frequently and maintain 40cm display distance."
    ),
    DENGUE(
        title = "Dengue / Fatigue Mode",
        hindiTitle = "आँखों पर तनाव (थकान)",
        maxHours = 6.0f,
        description = "4 to 6 hours. High eye-strain risk, dry corneas and headaches.",
        colorHex = 0xFFF59E0B, // Amber
        badgeIconName = "WarningAmber",
        eyeAdvice = "Warning: Screen fatigue detected. Switch on Read Mode / Blue Light Filter."
    ),
    DANGEROUS(
        title = "Dangerous Mode",
        hindiTitle = "गंभीर स्क्रीन लत",
        maxHours = 10.0f,
        description = "6 to 10 hours. Severe ocular stress, circadian sleep rhythm disruption.",
        colorHex = 0xFFEF4444, // Red
        badgeIconName = "Dangerous",
        eyeAdvice = "Severe strain! Close eyes for 15 minutes immediately."
    ),
    END_EYES(
        title = "End Eyes Mode",
        hindiTitle = "अति गंभीर जोखिम",
        maxHours = 24.0f,
        description = "Over 10 hours. Extreme myopia risk and photic exhaustion.",
        colorHex = 0xFF8B5CF6, // Purple / Crimson alert
        badgeIconName = "VisibilityOff",
        eyeAdvice = "Emergency: Disconnect immediately to avoid chronic vision impairment."
    );

    companion object {
        fun fromHours(hours: Float): ScreenTimeMode {
            return when {
                hours <= 2.0f -> SAFE
                hours <= 4.0f -> NORMAL
                hours <= 6.0f -> DENGUE
                hours <= 10.0f -> DANGEROUS
                else -> END_EYES
            }
        }
    }
}

data class AppUsageItem(
    val packageName: String,
    val appName: String,
    val foregroundTimeMs: Long,
    val lastUsedTimestamp: Long,
    val isUnused6Months: Boolean,
    val batteryDrainPercent: Float = 0f
)
