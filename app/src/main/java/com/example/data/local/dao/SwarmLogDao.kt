package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.SwarmLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SwarmLogDao {
  @Query("SELECT * FROM swarm_logs ORDER BY timestamp DESC")
  fun getAllSwarmLogs(): Flow<List<SwarmLogEntity>>

  @Query(
      "SELECT * FROM swarm_logs WHERE targetAgent = :agentName ORDER BY timestamp DESC LIMIT :limit")
  fun getLogsByAgent(agentName: String, limit: Int = 50): Flow<List<SwarmLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertLog(log: SwarmLogEntity): Long

  @Query("DELETE FROM swarm_logs") suspend fun clearLogs()
}
