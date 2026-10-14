package com.example.api

import android.graphics.Bitmap
import com.example.data.GeminiNativeClient

/**
 * Backwards-compatible avatar image entry point used by AuraViewModel.
 * The actual client owns API configuration and returns null when real image generation is unavailable.
 */
suspend fun generateAvatarImage(prompt: String): Bitmap? = GeminiNativeClient.generateImage(prompt)
