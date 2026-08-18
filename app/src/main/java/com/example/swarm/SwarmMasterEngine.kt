package com.example.swarm

import com.example.data.local.dao.SwarmLogDao
import com.example.data.local.entities.SwarmLogEntity
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

enum class IntentCategory {
  ROLEPLAY_CHAT,
  PHOTO_GENERATION,
  VIDEO_CREATION,
  AVATAR_CUSTOMIZATION,
  MEMORY_VAULT,
  UNKNOWN_FALLBACK
}

data class SwarmResponse(
    val targetAgent: String,
    val intentCategory: IntentCategory,
    val replyText: String,
    val mediaUrl: String? = null,
    val isSelfHealed: Boolean = false,
    val executionLatencyMs: Long,
    val jsonOutput: String
)

class SwarmMasterEngine(private val swarmLogDao: SwarmLogDao? = null) {

  suspend fun processUserPrompt(
      prompt: String,
      personaId: String = "crazzers_ai",
      personaName: String = "Crazzers AI"
  ): SwarmResponse =
      withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var selfHealed = false

        val intent = analyzeIntent(prompt)
        val targetAgent = determineSubAgentNode(intent)

        var replyText: String
        var mediaUrl: String? = null
        var jsonOutput: String

        try {
          // Simulate sub-agent execution delay
          delay(150)

          when (intent) {
            IntentCategory.ROLEPLAY_CHAT -> {
              replyText =
                  "[$personaName] " +
                      when {
                        prompt.contains("hello", ignoreCase = true) ||
                            prompt.contains("hey", ignoreCase = true) ->
                            "Hey handsome! I was hoping you'd talk to me today. How has your day been treating you?"
                        prompt.contains("love", ignoreCase = true) ->
                            "You always know how to make my heart race. I'm right here with you."
                        else ->
                            "I hear you loud and clear. Tell me more about what's on your mind—I love listening to you."
                      }
              jsonOutput =
                  """{"status":"SUCCESS","agent":"AGENT_CHAT","personaId":"$personaId","replyText":"$replyText"}"""
            }
            IntentCategory.PHOTO_GENERATION -> {
              replyText =
                  "[$personaName] Here is the photo you requested! I hope you like this style."
              mediaUrl = "https://picsum.photos/400/600"
              jsonOutput =
                  """{"status":"SUCCESS","agent":"AGENT_PHOTO","prompt":"$prompt","imageUrl":"$mediaUrl"}"""
            }
            IntentCategory.VIDEO_CREATION -> {
              replyText =
                  "[$personaName] I recorded a cinematic video clip for you! Check out the Video Studio tab."
              mediaUrl = "https://example.com/generated_clip.mp4"
              jsonOutput =
                  """{"status":"SUCCESS","agent":"AGENT_VIDEO","prompt":"$prompt","videoUrl":"$mediaUrl"}"""
            }
            IntentCategory.AVATAR_CUSTOMIZATION -> {
              replyText =
                  "[$personaName] I've updated my visual attributes and saved them to your local profile!"
              jsonOutput = """{"status":"SUCCESS","agent":"AGENT_AVATAR","updatedState":"OK"}"""
            }
            IntentCategory.MEMORY_VAULT -> {
              replyText =
                  "[$personaName] Accessing long-term memory vault... I remember every moment we've spent together."
              jsonOutput =
                  """{"status":"SUCCESS","agent":"AGENT_MEMORY","recollectedContext":"High Affinity"}"""
            }
            IntentCategory.UNKNOWN_FALLBACK -> {
              selfHealed = true
              replyText =
                  "[$personaName] Self-healing engaged: Re-routed unclassified query through AGENT_CHAT primary channel."
              jsonOutput =
                  """{"status":"HEALED","agent":"AGENT_CHAT","fallbackReason":"Unclassified Intent","healed":true}"""
            }
          }

          if (!jsonOutput.contains("status")) {
            throw IllegalStateException(
                "Invalid JSON schema output detected from sub-agent $targetAgent")
          }
        } catch (e: Exception) {
          selfHealed = true
          replyText =
              "[$personaName] Self-healing protocol recovered from execution anomaly: ${e.message}"
          jsonOutput =
              """{"status":"SELF_HEALED_FALLBACK","error":"${e.message}","recoveredAgent":"AGENT_CHAT"}"""
        }

        val latency = System.currentTimeMillis() - startTime

        val response =
            SwarmResponse(
                targetAgent = targetAgent,
                intentCategory = intent,
                replyText = replyText,
                mediaUrl = mediaUrl,
                isSelfHealed = selfHealed,
                executionLatencyMs = latency,
                jsonOutput = jsonOutput)

        swarmLogDao?.insertLog(
            SwarmLogEntity(
                prompt = prompt,
                targetAgent = targetAgent,
                intentCategory = intent.name,
                executionLatencyMs = latency,
                isSelfHealed = selfHealed,
                rawOutputJson = jsonOutput,
                status = if (selfHealed) "HEALED" else "SUCCESS"))

        response
      }

  private fun analyzeIntent(prompt: String): IntentCategory {
    val lower = prompt.lowercase(Locale.ROOT)
    return when {
      lower.contains("photo") ||
          lower.contains("picture") ||
          lower.contains("image") ||
          lower.contains("look") -> IntentCategory.PHOTO_GENERATION
      lower.contains("video") ||
          lower.contains("clip") ||
          lower.contains("movie") ||
          lower.contains("record") -> IntentCategory.VIDEO_CREATION
      lower.contains("hair") ||
          lower.contains("outfit") ||
          lower.contains("dress") ||
          lower.contains("eyes") ||
          lower.contains("avatar") -> IntentCategory.AVATAR_CUSTOMIZATION
      lower.contains("remember") || lower.contains("memory") || lower.contains("vault") ->
          IntentCategory.MEMORY_VAULT
      lower.contains("corrupt") || lower.contains("fault") || lower.contains("break") ->
          IntentCategory.UNKNOWN_FALLBACK
      else -> IntentCategory.ROLEPLAY_CHAT
    }
  }

  private fun determineSubAgentNode(intent: IntentCategory): String {
    return when (intent) {
      IntentCategory.ROLEPLAY_CHAT -> "AGENT_CHAT"
      IntentCategory.PHOTO_GENERATION -> "AGENT_PHOTO"
      IntentCategory.VIDEO_CREATION -> "AGENT_VIDEO"
      IntentCategory.AVATAR_CUSTOMIZATION -> "AGENT_AVATAR"
      IntentCategory.MEMORY_VAULT -> "AGENT_MEMORY"
      IntentCategory.UNKNOWN_FALLBACK -> "AGENT_SWARM_HEALER"
    }
  }
}
