package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryDao {
    @Query("SELECT * FROM battery_cycle_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<BatteryCycleLog>>

    @Query("SELECT * FROM battery_cycle_logs WHERE timestamp >= :sinceTimestamp ORDER BY timestamp ASC")
    fun getLogsSince(sinceTimestamp: Long): Flow<List<BatteryCycleLog>>

    @Query("SELECT COUNT(*) FROM battery_cycle_logs WHERE isCharging = 1 AND timestamp >= :startOfDay")
    suspend fun getTodayChargeEventsCount(startOfDay: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BatteryCycleLog): Long

    @Query("DELETE FROM battery_cycle_logs WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)

    @Query("DELETE FROM battery_cycle_logs")
    suspend fun clearAllLogs()
}
