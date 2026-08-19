package com.example.data.models

import java.util.UUID

enum class MessageSender {
    USER,
    AI,
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val personaId: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
)

data class PersonaModel(
    val id: String,
    val name: String,
    val tagline: String,
    val description: String,
    val styleVibe: String,
    val defaultGreeting: String,
    val primaryColorHex: Long,
)

data class AvatarCustomizationState(
    val hairStyle: String = "Long Waves",
    val hairColor: String = "Platinum Blonde",
    val eyeColor: String = "Emerald Green",
    val outfit: String = "Luxury Silk Gown",
    val accessory: String = "Diamond Choker",
    val skinTone: String = "Warm Porcelain",
    val backgroundVibe: String = "Sunset Penthouse",
)

enum class CameraMotion(val displayName: String) {
    PAN("Pan Right"),
    TILT("Tilt Up"),
    ZOOM("Slow Zoom In"),
    DOLLY("Dolly Tracking"),
    STATIC("Cinematic Static"),
}

data class VideoScene(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val durationSec: Int = 10,
    val cameraMotion: CameraMotion = CameraMotion.ZOOM,
    val currentPositionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val characterName: String = "Crazzers AI",
)

data class SwarmHealthStatus(
    val status: String = "healthy",
    val selfHealed: Boolean = false,
    val healingLog: String = "All nodes operating nominal",
    val activeAgents: List<String> =
        listOf("AGENT_CHAT", "AGENT_PHOTO", "AGENT_VIDEO", "AGENT_AVATAR", "AGENT_MEMORY"),
)
