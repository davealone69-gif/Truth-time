import sys

filepath = 'app/src/main/java/com/example/viewmodel/AuraViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

start_str = "private suspend fun generateAiResponse(userMessage: String) {"
end_str = "viewModelScope.launch { dataStoreManager.saveChatHistory(updatedList) }\n  }"

start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx) + len(end_str)

replacement = """private suspend fun generateAiResponse(userMessage: String) {
    _isAiTyping.value = true
    val persona = _activePersona.value
    var localImgPath: String? = null
    var localVideoPath: String? = null

    val aiReplyText = if (userMessage.contains("photo", ignoreCase = true) || userMessage.contains("picture", ignoreCase = true) || userMessage.contains("look", ignoreCase = true)) {
        _typingStatusText.value = "AGENT_PHOTO generating image..."
        val prompt = "A photo of ${persona.name}, ${_companionPersonaMatrix.value.environmentVibe}, ${_companionPersonaMatrix.value.personalityArchetype}. ${_avatarState.value.outfit}, ${_avatarState.value.hairColor} hair."
        val bitmap = GeminiNativeClient.generateImage(prompt)
        if (bitmap != null) {
            val file = java.io.File(getApplication<android.app.Application>().filesDir, "gen_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
            localImgPath = file.absolutePath
            "Here's the photo you requested!"
        } else {
            "I couldn't generate a photo right now."
        }
    } else if (userMessage.contains("video", ignoreCase = true)) {
        _typingStatusText.value = "AGENT_VIDEO generating video..."
        val prompt = "A cinematic video of ${persona.name}, ${_companionPersonaMatrix.value.environmentVibe}, ${_companionPersonaMatrix.value.personalityArchetype}."
        val videoUrl = GeminiNativeClient.generateVideo(prompt)
        if (videoUrl != null) {
            localVideoPath = videoUrl
            "Here's the video clip!"
        } else {
            "I couldn't generate a video right now."
        }
    } else {
        _typingStatusText.value = "AGENT_CHAT generating reply..."
        val contextPrompt = "You are ${persona.name}, with vibe: ${_companionPersonaMatrix.value.environmentVibe}, personality: ${_companionPersonaMatrix.value.personalityArchetype}. The user says: $userMessage"
        GeminiNativeClient.generateText(contextPrompt, "You are a roleplay companion.") ?: "Sorry, I couldn't connect right now."
    }

    val aiMessage = ChatMessage(sender = MessageSender.AI, text = aiReplyText, personaId = persona.id, imageUrl = localImgPath, videoUrl = localVideoPath)
    val updatedList = _messages.value + aiMessage
    _messages.value = updatedList
    _isAiTyping.value = false

    viewModelScope.launch { dataStoreManager.saveChatHistory(updatedList) }
  }"""

if start_idx != -1:
    new_content = content[:start_idx] + replacement + content[end_idx:]
    with open(filepath, 'w') as f:
        f.write(new_content)
    print("Patched generateAiResponse")
else:
    print("Could not find generateAiResponse")
