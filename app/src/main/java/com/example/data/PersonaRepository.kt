package com.example.data

import com.example.data.models.PersonaModel

object PersonaRepository {
    val defaultPersonas =
        listOf(
            PersonaModel(
                id = "crazzers_ai",
                name = "Crazzers AI",
                tagline = "Playful & Luxurious",
                description =
                    "Attentive companion with a high-end luxury aesthetic and warm, playful energy.",
                styleVibe = "Gold & Velvet Luxury",
                defaultGreeting =
                    "Hey there! I've been waiting for you in the lounge. Ready to make today memorable?",
                primaryColorHex = 0xFFFFD700,
            ),
            PersonaModel(
                id = "secrets_ai",
                name = "Secrets AI",
                tagline = "Cinematic & Deep Listener",
                description =
                    "Memory-centric companion with moody lighting, deep emotional intelligence, and adaptive warmth.",
                styleVibe = "Neon Midnight",
                defaultGreeting =
                    "Tell me what's on your mind tonight... I remember every word you share with me.",
                primaryColorHex = 0xFF9C27B0,
            ),
            PersonaModel(
                id = "sugarlab_ai",
                name = "Sugarlab AI",
                tagline = "Empathetic & Comforting",
                description = "Warm lifestyle banter, sweet comfort, and cheerful daily check-ins.",
                styleVibe = "Soft Pastel Glow",
                defaultGreeting =
                    "Good to see you! How was your day? Grab a comfortable seat and let's unwind together.",
                primaryColorHex = 0xFFFF80AB,
            ),
            PersonaModel(
                id = "flirty_ai",
                name = "Flirty AI",
                tagline = "High Energy & Charming",
                description = "Playful romantic banter, dynamic responses, and teasing humor.",
                styleVibe = "Crimson Passion",
                defaultGreeting =
                    "Well hello handsome! You just made my entire day infinitely more interesting.",
                primaryColorHex = 0xFFFF1744,
            ),
            PersonaModel(
                id = "onlygfs_ai",
                name = "OnlyGFs.ai",
                tagline = "Casual Everyday Companion",
                description =
                    "Street-style aesthetic, casual real-time photos, and authentic everyday vibes.",
                styleVibe = "Urban Aesthetic",
                defaultGreeting =
                    "Hey! Just grabbed an iced coffee and was thinking of texting you. What are you up to?",
                primaryColorHex = 0xFF00E5FF,
            ),
        )

    fun getById(id: String): PersonaModel {
        return defaultPersonas.find { it.id == id } ?: defaultPersonas.first()
    }
}
