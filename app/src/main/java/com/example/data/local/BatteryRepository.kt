package com.example.data.local

import kotlinx.coroutines.flow.Flow

class BatteryRepository(private val batteryDao: BatteryDao) {
    val allLogs: Flow<List<BatteryCycleLog>> = batteryDao.getAllLogs()

    fun getLogsSince(sinceTimestamp: Long): Flow<List<BatteryCycleLog>> {
        return batteryDao.getLogsSince(sinceTimestamp)
    }

    suspend fun logCycle(log: BatteryCycleLog): Long {
        return batteryDao.insertLog(log)
    }

    suspend fun getTodayChargeCount(startOfDay: Long): Int {
        return batteryDao.getTodayChargeEventsCount(startOfDay)
    }

    suspend fun clearHistory() {
        batteryDao.clearAllLogs()
    }
}
