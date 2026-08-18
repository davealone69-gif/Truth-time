package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val personaId: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null
)

@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val tagline: String,
    val description: String,
    val styleVibe: String,
    val defaultGreeting: String,
    val primaryColorHex: Long,
    val isCustom: Boolean = false
)

@Entity(tableName = "swarm_logs")
data class SwarmLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val prompt: String,
    val targetAgent: String,
    val intentCategory: String,
    val executionLatencyMs: Long,
    val isSelfHealed: Boolean = false,
    val rawOutputJson: String,
    val status: String = "SUCCESS"
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = "default_user",
    val username: String = "Master",
    val relationshipLevel: Int = 1,
    val affinityScore: Int = 100,
    val preferredPersonaId: String = "crazzers_ai"
)

@Entity(tableName = "media_assets")
data class MediaAssetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val personaId: String,
    val mediaType: String,
    val urlOrPath: String,
    val prompt: String,
    val timestamp: Long = System.currentTimeMillis()
)
