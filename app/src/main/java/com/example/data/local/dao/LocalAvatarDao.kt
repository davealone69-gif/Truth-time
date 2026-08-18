package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.LocalAvatarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalAvatarDao {

  @Query("SELECT * FROM local_avatars ORDER BY createdAt DESC")
  fun getAllAvatars(): Flow<List<LocalAvatarEntity>>

  @Query("SELECT * FROM local_avatars WHERE modelId = :modelId")
  suspend fun getAvatarById(modelId: String): LocalAvatarEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAvatar(avatar: LocalAvatarEntity)

  @Query("DELETE FROM local_avatars WHERE modelId = :modelId")
  suspend fun deleteAvatarById(modelId: String)
}
