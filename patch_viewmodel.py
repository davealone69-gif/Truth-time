import sys

filepath = 'app/src/main/java/com/example/viewmodel/AuraViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

import_target = "import com.example.data.DataStoreManager"
import_replace = """import com.example.data.DataStoreManager
import com.example.data.GeminiNativeClient
import android.graphics.Bitmap"""
if import_target in content:
    content = content.replace(import_target, import_replace)

init_target = "init {"
init_replace = """private val _apiKey = MutableStateFlow("")
  val apiKey: StateFlow<String> = _apiKey.asStateFlow()

  fun setApiKey(key: String) {
    viewModelScope.launch {
      dataStoreManager.saveApiKey(key)
    }
  }

  init {
    viewModelScope.launch {
      dataStoreManager.apiKeyFlow.collect { key ->
        _apiKey.value = key
        GeminiNativeClient.API_KEY = key
      }
    }"""
if "val apiKey" not in content and init_target in content:
    content = content.replace(init_target, init_replace)

generate_target = """  private suspend fun generateAiResponse(userMessage: String) {
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
              "Here's a video of me checking out the vibe."
            } else {
              "Hey there! I'm Crazzers, your custom AI companion. I noticed you're talking about '${userMessage}'. How's your day going?"
            }
          }
          else -> {
            "I'm ${persona.name}. You said: '${userMessage}'. Let's chat!"
          }
        }"""

generate_replace = """  private suspend fun generateAiResponse(userMessage: String) {
    _isAiTyping.value = true
    _typingStatusText.value = "AGENT_CHAT generating reply..."
    
    val persona = _activePersona.value
    val contextPrompt = "You are ${persona.name}, with vibe: ${_companionPersonaMatrix.value.environmentVibe}, personality: ${_companionPersonaMatrix.value.personalityArchetype}. The user says: $userMessage"
    
    val response = GeminiNativeClient.generateText(contextPrompt, "You are a roleplay companion.")
    val aiReplyText = response ?: "Sorry, I couldn't connect right now." """

if generate_target in content:
    content = content.replace(generate_target, generate_replace)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated AuraViewModel")
