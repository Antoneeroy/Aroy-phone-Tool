package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_cycle_logs")
data class BatteryCycleLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val batteryLevel: Int,
    val voltageMv: Int,
    val currentMa: Int,
    val temperatureC: Float,
    val wattage: Float,
    val plugType: String, // "AC", "USB", "Wireless", "Battery"
    val isCharging: Boolean,
    val healthStatus: String,
    val sessionType: String = "DAILY_LOG" // "CHARGE_SESSION", "OVERHEAT_EVENT", "DAILY_LOG"
)
