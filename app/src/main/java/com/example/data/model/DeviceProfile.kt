package com.example.data.model

import android.os.Build

data class SysfsPaths(
    val currentNowPaths: List<String>,
    val voltageNowPaths: List<String>,
    val tempPaths: List<String>,
    val chargeCounterPaths: List<String>,
    val chargeTypePaths: List<String>
)

data class DeviceProfile(
    val manufacturer: String,
    val brandName: String,
    val safeMaxCurrentMa: Int,
    val maxSafeTempC: Float,
    val paths: SysfsPaths
) {
    companion object {
        val PIXEL = DeviceProfile(
            manufacturer = "Google",
            brandName = "Pixel",
            safeMaxCurrentMa = 4500,
            maxSafeTempC = 42.0f,
            paths = SysfsPaths(
                currentNowPaths = listOf(
                    "/sys/class/power_supply/battery/current_now",
                    "/sys/class/power_supply/bms/current_now"
                ),
                voltageNowPaths = listOf(
                    "/sys/class/power_supply/battery/voltage_now",
                    "/sys/class/power_supply/bms/voltage_now"
                ),
                tempPaths = listOf(
                    "/sys/class/power_supply/battery/temp",
                    "/sys/class/thermal/thermal_zone0/temp"
                ),
                chargeCounterPaths = listOf(
                    "/sys/class/power_supply/battery/charge_counter"
                ),
                chargeTypePaths = listOf(
                    "/sys/class/power_supply/battery/charge_type"
                )
            )
        )

        val SAMSUNG = DeviceProfile(
            manufacturer = "Samsung",
            brandName = "Galaxy",
            safeMaxCurrentMa = 5000,
            maxSafeTempC = 42.0f,
            paths = SysfsPaths(
                currentNowPaths = listOf(
                    "/sys/class/power_supply/sec-battery/current_now",
                    "/sys/class/power_supply/battery/current_now"
                ),
                voltageNowPaths = listOf(
                    "/sys/class/power_supply/sec-battery/voltage_now",
                    "/sys/class/power_supply/battery/voltage_now"
                ),
                tempPaths = listOf(
                    "/sys/class/power_supply/sec-battery/temp",
                    "/sys/class/power_supply/battery/temp"
                ),
                chargeCounterPaths = listOf(
                    "/sys/class/power_supply/sec-battery/charge_counter",
                    "/sys/class/power_supply/battery/charge_counter"
                ),
                chargeTypePaths = listOf(
                    "/sys/class/power_supply/sec-battery/status"
                )
            )
        )

        val XIAOMI = DeviceProfile(
            manufacturer = "Xiaomi",
            brandName = "Xiaomi / Redmi / POCO",
            safeMaxCurrentMa = 6000,
            maxSafeTempC = 42.0f,
            paths = SysfsPaths(
                currentNowPaths = listOf(
                    "/sys/class/power_supply/battery/current_now",
                    "/sys/class/qcom-battery/current_now"
                ),
                voltageNowPaths = listOf(
                    "/sys/class/power_supply/battery/voltage_now",
                    "/sys/class/qcom-battery/voltage_now"
                ),
                tempPaths = listOf(
                    "/sys/class/power_supply/battery/temp",
                    "/sys/class/thermal/thermal_zone1/temp"
                ),
                chargeCounterPaths = listOf(
                    "/sys/class/power_supply/battery/charge_counter"
                ),
                chargeTypePaths = listOf(
                    "/sys/class/power_supply/battery/charge_type"
                )
            )
        )

        val OPPO = DeviceProfile(
            manufacturer = "OPPO",
            brandName = "OPPO / OnePlus / Realme",
            safeMaxCurrentMa = 6500,
            maxSafeTempC = 42.0f,
            paths = SysfsPaths(
                currentNowPaths = listOf(
                    "/sys/class/power_supply/battery/current_now",
                    "/sys/class/power_supply/fastcharge/current_now"
                ),
                voltageNowPaths = listOf(
                    "/sys/class/power_supply/battery/voltage_now"
                ),
                tempPaths = listOf(
                    "/sys/class/power_supply/battery/temp"
                ),
                chargeCounterPaths = listOf(
                    "/sys/class/power_supply/battery/charge_counter"
                ),
                chargeTypePaths = listOf(
                    "/sys/class/power_supply/battery/fast_charge_status"
                )
            )
        )

        val GENERIC = DeviceProfile(
            manufacturer = "Generic",
            brandName = "Android Device",
            safeMaxCurrentMa = 4000,
            maxSafeTempC = 42.0f,
            paths = SysfsPaths(
                currentNowPaths = listOf("/sys/class/power_supply/battery/current_now"),
                voltageNowPaths = listOf("/sys/class/power_supply/battery/voltage_now"),
                tempPaths = listOf("/sys/class/power_supply/battery/temp"),
                chargeCounterPaths = listOf("/sys/class/power_supply/battery/charge_counter"),
                chargeTypePaths = listOf("/sys/class/power_supply/battery/status")
            )
        )

        fun resolveCurrentDevice(): DeviceProfile {
            val m = Build.MANUFACTURER.lowercase()
            return when {
                m.contains("google") -> PIXEL
                m.contains("samsung") -> SAMSUNG
                m.contains("xiaomi") || m.contains("redmi") || m.contains("poco") -> XIAOMI
                m.contains("oppo") || m.contains("oneplus") || m.contains("realme") -> OPPO
                else -> GENERIC
            }
        }
    }
}
