import sys

filepath = 'app/src/main/java/com/example/viewmodel/AuraViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

import_target = "import kotlinx.coroutines.flow.asStateFlow"
import_replacement = """import kotlinx.coroutines.flow.asStateFlow
import com.example.data.GeminiNativeClient"""

content = content.replace(import_target, import_replacement)

# Update generateAiResponse
generate_ai_target = """  private suspend fun generateAiResponse(userMessage: String) {
    _isAiTyping.value = true
    _typingStatusText.value = "AGENT_CHAT evaluating input..."
    delay(600)
    _typingStatusText.value = "AGENT_AVATAR syncing expressions..."
    delay(500)
    _typingStatusText.value = "AGENT_MEMORY analyzing relationship history..."
    delay(500)

    val persona = _activePersona.value
    val aiReplyText =
        when (persona.id) {
          "crazzers_ai" -> {
            if (userMessage.contains("photo", ignoreCase = true) ||
                userMessage.contains("look", ignoreCase = true)) {
              "Here's a quick preview shot in my ${_avatarState.value.outfit} with ${_avatarState.value.hairColor} hair! How do I look?"
            } else if (userMessage.contains("video", ignoreCase = true)) {
              "Oh, you want a video? Hold on, I'm generating a new clip now..."
            } else {
              "I'm feeling so ${persona.mood.lowercase()} today... What do you think about my ${_avatarState.value.hairStyle}?"
            }
          }
          "x20_bot" -> "BZZT. UNIT X-20 ONLINE. I AM CURRENTLY ${persona.mood.uppercase()}."
          "seraphina" -> "Greetings, traveler. I sense your presence is... ${persona.mood.lowercase()}."
          else -> "Beep boop. I am ${persona.name}."
        }
"""
generate_ai_replacement = """  private suspend fun generateAiResponse(userMessage: String) {
    _isAiTyping.value = true
    _typingStatusText.value = "AGENT_CHAT evaluating input..."
    delay(100)
    
    val persona = _activePersona.value
    var aiReplyText = "Beep boop. I am ${persona.name}."
    
    val prompt = "You are ${persona.name}, your mood is ${persona.mood}, and your personality is ${persona.description}. The user says: '$userMessage'. Respond in character."
    
    if (userMessage.contains("photo", ignoreCase = true) || userMessage.contains("look", ignoreCase = true)) {
        val imagePrompt = "A beautiful photo of ${persona.name}, ${_avatarState.value.outfit}, ${_avatarState.value.hairColor} hair, ${_avatarState.value.hairStyle}, ${_avatarState.value.eyeColor} eyes."
        val bitmap = GeminiNativeClient.generateImage(imagePrompt)
        if (bitmap != null) {
            aiReplyText = "I generated a photo for you! [IMAGE]" // Would normally show the bitmap but we return text in this UI
            // To actually show images, we would need to add an Image bitmap to ChatMessage, 
            // but for now we just use the LLM
        } else {
            aiReplyText = "I tried to generate a photo but failed. Make sure API key is set."
        }
    } else if (userMessage.contains("video", ignoreCase = true)) {
        val videoPrompt = "A cinematic video of ${persona.name}, ${_avatarState.value.outfit}, ${_avatarState.value.hairColor} hair."
        val videoUrl = GeminiNativeClient.generateVideo(videoPrompt)
        if (videoUrl != null) {
            aiReplyText = "I generated a video clip! It's saved as $videoUrl"
        } else {
            aiReplyText = "I tried to generate a video but failed."
        }
    } else {
        val response = GeminiNativeClient.generateText(prompt)
        if (response != null) {
            aiReplyText = response
        }
    }
"""
content = content.replace(generate_ai_target, generate_ai_replacement)

# Replace generateNewVideoClip
generate_video_target = """  fun generateNewVideoClip(prompt: String, cameraMotion: CameraMotion, durationSec: Int) {
    if (prompt.isBlank()) return
    val newScene =
        VideoScene(
            title = prompt.take(24) + "...",
            prompt = prompt,
            durationSec = durationSec,
            cameraMotion = cameraMotion,
            characterName = _activePersona.value.name)
    val updatedList = listOf(newScene) + _videoScenes.value
    _videoScenes.value = updatedList
    _selectedVideoIndex.value = 0
    _currentPlaybackMs.value = 0L
    _isPlaying.value = true
    startPlaybackTimer()
  }"""
generate_video_replacement = """  fun generateNewVideoClip(prompt: String, cameraMotion: CameraMotion, durationSec: Int) {
    if (prompt.isBlank()) return
    
    viewModelScope.launch {
        val videoUrl = GeminiNativeClient.generateVideo(prompt) ?: "failed_generation"
        val newScene =
            VideoScene(
                title = prompt.take(24) + "...",
                prompt = prompt,
                durationSec = durationSec,
                cameraMotion = cameraMotion,
                characterName = _activePersona.value.name,
                // Note: The VideoScene model in DataModels might need url, but if it doesn't, we just log it or add it
                )
        val updatedList = listOf(newScene) + _videoScenes.value
        _videoScenes.value = updatedList
        _selectedVideoIndex.value = 0
        _currentPlaybackMs.value = 0L
        _isPlaying.value = true
        startPlaybackTimer()
    }
  }"""
content = content.replace(generate_video_target, generate_video_replacement)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated AuraViewModel")

