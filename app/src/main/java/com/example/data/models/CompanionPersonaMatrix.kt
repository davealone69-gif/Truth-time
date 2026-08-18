package com.example.data.models

data class CompanionPersonaMatrix(
    val avatarId: String = "custom_model_advanced",

    // Vibe & Atmosphere
    val environmentVibe: String = "Cyberpunk Neon Alley / Luxury Penthouse Suite",
    val atmosphericLighting: String = "Moody cinematic key lighting with soft rim highlights",

    // Personality & Core Trait Archetype
    val personalityArchetype: String = "Playful & Luxurious",
    val conversationalTone: String = "Witty, attentive, deeply engaging, and responsive",
    val psychologicalProfile: String =
        "High empathy, tailored to user preferences, adaptive memory",

    // Cosplay & Thematic Wardrobe Options
    val cosplayTheme: String = "Sci-Fi Cyberpunk Mercenary / Fantasy Elf Warrior / Maid Uniform",
    val currentOutfitState: String = "Custom Cosplay / Lingerie",
    val isNudeModeActive: Boolean = false,

    // Attitude & Behavioral Stance
    val attitude: String = "Confident, unapologetically alluring, fiercely loyal, and responsive",
    val socialDynamic: String = "Companion-led immersion with real-time trait adjustments"
)
