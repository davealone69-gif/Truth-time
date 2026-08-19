package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_avatars")
data class LocalAvatarEntity(
    @PrimaryKey val modelId: String,
    val modelName: String = "",
    val bodyType: String = "",
    val wardrobeState: String = "",
    val vibeSetting: String = "",
    val localPhotoPath: String? = null,
    val isUnconstrainedActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)
