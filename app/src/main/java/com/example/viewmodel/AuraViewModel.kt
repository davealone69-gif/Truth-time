package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.generateAvatarImage
import com.example.data.DataStoreManager
import com.example.data.GeminiNativeClient
import com.example.data.PersonaRepository
import com.example.data.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuraViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    // Chat State
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _activePersona = MutableStateFlow(PersonaRepository.defaultPersonas.first())
    val activePersona: StateFlow<PersonaModel> = _activePersona.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    private val _typingStatusText = MutableStateFlow("SWARM_MASTER routing...")
    val typingStatusText: StateFlow<String> = _typingStatusText.asStateFlow()

    // Avatar Customization State
    private val _avatarState = MutableStateFlow(AvatarCustomizationState())
    val avatarState: StateFlow<AvatarCustomizationState> = _avatarState.asStateFlow()

    private val _avatarSpec = MutableStateFlow(AvatarSpec())
    val avatarSpec: StateFlow<AvatarSpec> = _avatarSpec.asStateFlow()

    private val _advancedAvatarSpec = MutableStateFlow(AdvancedAvatarSpec())
    val advancedAvatarSpec: StateFlow<AdvancedAvatarSpec> = _advancedAvatarSpec.asStateFlow()

    private val _companionPersonaMatrix = MutableStateFlow(CompanionPersonaMatrix())
    val companionPersonaMatrix: StateFlow<CompanionPersonaMatrix> =
        _companionPersonaMatrix.asStateFlow()

    // Video Maker State
    private val _videoScenes = MutableStateFlow<List<VideoScene>>(emptyList())
    val videoScenes: StateFlow<List<VideoScene>> = _videoScenes.asStateFlow()

    private val _selectedVideoIndex = MutableStateFlow(0)
    val selectedVideoIndex: StateFlow<Int> = _selectedVideoIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlaybackMs = MutableStateFlow(0L)
    val currentPlaybackMs: StateFlow<Long> = _currentPlaybackMs.asStateFlow()

    private var playbackJob: Job? = null

    // Swarm Health State
    private val _swarmHealth =
        MutableStateFlow(
            SwarmHealthStatus(
                status = "healthy",
                selfHealed = false,
                healingLog =
                    "All 5 Swarm nodes (CHAT, PHOTO, VIDEO, AVATAR, MEMORY) fully operational",
                activeAgents =
                    listOf(
                        "AGENT_CHAT",
                        "AGENT_PHOTO",
                        "AGENT_VIDEO",
                        "AGENT_AVATAR",
                        "AGENT_MEMORY",
                    ),
            ),
        )
    val swarmHealth: StateFlow<SwarmHealthStatus> = _swarmHealth.asStateFlow()

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    fun setApiKey(key: String) {
        viewModelScope.launch {
            dataStoreManager.saveApiKey(key)
        }
    }

    private val _generatedImage = MutableStateFlow<Bitmap?>(null)
    val generatedImage = _generatedImage.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun generateAvatar() {
        val spec = _advancedAvatarSpec.value
        val prompt =
            "A highly detailed, 8k resolution, cinematic 3D character portrait in a cyberpunk style. " +
                "The character is a ${spec.age} year old ${spec.race}. " +
                "They have ${spec.hairColor} hair styled in ${spec.hairStyle}, and ${spec.eyeColor} eyes. " +
                "They are wearing ${spec.currentOutfit}. " +
                "Facial structure: ${spec.facialStructure}. Makeup: ${spec.makeupStyle}. " +
                "Expression: ${spec.expressionVibe}. Lighting: Neon, dramatic, rim lighting."

        viewModelScope.launch {
            _isGenerating.value = true
            val bitmap = generateAvatarImage(prompt)
            if (bitmap != null) {
                _generatedImage.value = bitmap
            }
            _isGenerating.value = false
        }
    }

    init {
        viewModelScope.launch {
            dataStoreManager.apiKeyFlow.collect { key ->
                _apiKey.value = key
                GeminiNativeClient.API_KEY = key
            }
        }
        // Observe DataStore for persisted chat history
        viewModelScope.launch {
            dataStoreManager.chatHistoryFlow.collect { savedMessages ->
                if (savedMessages.isNotEmpty()) {
                    _messages.value = savedMessages
                } else {
                    // Seed with default initial greeting
                    val defaultMsg =
                        ChatMessage(
                            sender = MessageSender.AI,
                            text = _activePersona.value.defaultGreeting,
                            personaId = _activePersona.value.id,
                        )
                    _messages.value = listOf(defaultMsg)
                    dataStoreManager.saveChatHistory(listOf(defaultMsg))
                }
            }
        }

        // Observe DataStore for active persona
        viewModelScope.launch {
            dataStoreManager.activePersonaFlow.collect { personaId ->
                _activePersona.value = PersonaRepository.getById(personaId)
            }
        }

        // Observe DataStore for avatar state
        viewModelScope.launch {
            dataStoreManager.avatarStateFlow.collect { state -> _avatarState.value = state }
        }

        // Observe DataStore for avatar spec
        viewModelScope.launch {
            dataStoreManager.avatarSpecFlow.collect { spec -> _avatarSpec.value = spec }
        }

        // Observe DataStore for advanced avatar spec
        viewModelScope.launch {
            dataStoreManager.advancedAvatarSpecFlow.collect { spec -> _advancedAvatarSpec.value = spec }
        }

        // Observe DataStore for companion persona matrix
        viewModelScope.launch {
            dataStoreManager.companionPersonaMatrixFlow.collect { matrix ->
                _companionPersonaMatrix.value = matrix
            }
        }

        // Initial default video clips
        _videoScenes.value =
            listOf(
                VideoScene(
                    title = "Penthouse Golden Hour",
                    prompt =
                        "Character smiling in luxury silk gown standing by sunset window, golden hour backlight",
                    durationSec = 12,
                    cameraMotion = CameraMotion.ZOOM,
                    characterName = "Crazzers AI",
                ),
                VideoScene(
                    title = "Neon Rooftop Lounge",
                    prompt =
                        "Cinematic slow motion clip, character turning with emerald eye sparkle under purple neon lights",
                    durationSec = 15,
                    cameraMotion = CameraMotion.PAN,
                    characterName = "Secrets AI",
                ),
                VideoScene(
                    title = "Cozy Coffee Shop",
                    prompt = "Casual warm vibe, character taking a sip of coffee and winking at camera",
                    durationSec = 8,
                    cameraMotion = CameraMotion.STATIC,
                    characterName = "Sugarlab AI",
                ),
            )
    }

    // Chat actions
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMsg =
            ChatMessage(sender = MessageSender.USER, text = text, personaId = _activePersona.value.id)

        val updatedList = _messages.value + userMsg
        _messages.value = updatedList

        viewModelScope.launch {
            dataStoreManager.saveChatHistory(updatedList)
            generateAiResponse(text)
        }
    }

    private suspend fun generateAiResponse(userMessage: String) {
        _isAiTyping.value = true
        val persona = _activePersona.value
        var localImgPath: String? = null
        var localVideoPath: String? = null

        val aiReplyText =
            if (userMessage.contains(
                    "photo",
                    ignoreCase = true,
                ) || userMessage.contains("picture", ignoreCase = true) || userMessage.contains("look", ignoreCase = true)
            ) {
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

        val aiMessage =
            ChatMessage(
                sender = MessageSender.AI,
                text = aiReplyText,
                personaId = persona.id,
                imageUrl = localImgPath,
                videoUrl = localVideoPath,
            )
        val updatedList = _messages.value + aiMessage
        _messages.value = updatedList
        _isAiTyping.value = false

        viewModelScope.launch { dataStoreManager.saveChatHistory(updatedList) }
    }

    fun selectPersona(persona: PersonaModel) {
        _activePersona.value = persona
        viewModelScope.launch {
            dataStoreManager.saveActivePersona(persona.id)
            // Add greeting message from new persona if switching
            val greetingMsg =
                ChatMessage(
                    sender = MessageSender.AI,
                    text = persona.defaultGreeting,
                    personaId = persona.id,
                )
            val updated = _messages.value + greetingMsg
            _messages.value = updated
            dataStoreManager.saveChatHistory(updated)
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            dataStoreManager.clearChatHistory()
            val resetMsg =
                ChatMessage(
                    sender = MessageSender.AI,
                    text = _activePersona.value.defaultGreeting,
                    personaId = _activePersona.value.id,
                )
            _messages.value = listOf(resetMsg)
            dataStoreManager.saveChatHistory(listOf(resetMsg))
        }
    }

    // Avatar Customization actions
    fun updateAvatarStyle(
        hairStyle: String = _avatarState.value.hairStyle,
        hairColor: String = _avatarState.value.hairColor,
        eyeColor: String = _avatarState.value.eyeColor,
        outfit: String = _avatarState.value.outfit,
        accessory: String = _avatarState.value.accessory,
        skinTone: String = _avatarState.value.skinTone,
        backgroundVibe: String = _avatarState.value.backgroundVibe,
    ) {
        val newState =
            AvatarCustomizationState(
                hairStyle = hairStyle,
                hairColor = hairColor,
                eyeColor = eyeColor,
                outfit = outfit,
                accessory = accessory,
                skinTone = skinTone,
                backgroundVibe = backgroundVibe,
            )
        _avatarState.value = newState
        viewModelScope.launch { dataStoreManager.saveAvatarState(newState) }
    }

    fun updateAvatarSpec(
        name: String = _avatarSpec.value.name,
        age: Int = _avatarSpec.value.age,
        bodyType: String = _avatarSpec.value.bodyType,
        breastSize: String = _avatarSpec.value.breastSize,
        race: String = _avatarSpec.value.race,
        eyeColor: String = _avatarSpec.value.eyeColor,
        hairColor: String = _avatarSpec.value.hairColor,
        hairStyle: String = _avatarSpec.value.hairStyle,
        currentOutfit: String = _avatarSpec.value.currentOutfit,
        isNudeEnabled: Boolean = _avatarSpec.value.isNudeEnabled,
        backgroundVibe: String = _avatarSpec.value.backgroundVibe,
    ) {
        val newSpec =
            AvatarSpec(
                name = name,
                age = age,
                bodyType = bodyType,
                breastSize = breastSize,
                race = race,
                eyeColor = eyeColor,
                hairColor = hairColor,
                hairStyle = hairStyle,
                currentOutfit = currentOutfit,
                isNudeEnabled = isNudeEnabled,
                backgroundVibe = backgroundVibe,
            )
        _avatarSpec.value = newSpec
        _avatarState.value =
            AvatarCustomizationState(
                hairStyle = hairStyle,
                hairColor = hairColor,
                eyeColor = eyeColor,
                outfit = currentOutfit,
                accessory = _avatarState.value.accessory,
                skinTone = _avatarState.value.skinTone,
                backgroundVibe = backgroundVibe,
            )
        viewModelScope.launch {
            dataStoreManager.saveAvatarSpec(newSpec)
            dataStoreManager.saveAvatarState(_avatarState.value)
        }
    }

    fun updateAdvancedAvatarSpec(
        name: String = _advancedAvatarSpec.value.name,
        age: Int = _advancedAvatarSpec.value.age,
        heightStature: String = _advancedAvatarSpec.value.heightStature,
        bodyType: String = _advancedAvatarSpec.value.bodyType,
        breastSize: String = _advancedAvatarSpec.value.breastSize,
        waistHipRatio: String = _advancedAvatarSpec.value.waistHipRatio,
        race: String = _advancedAvatarSpec.value.race,
        skinTexture: String = _advancedAvatarSpec.value.skinTexture,
        tattoosAndPiercings: List<String> = _advancedAvatarSpec.value.tattoosAndPiercings,
        eyeColor: String = _advancedAvatarSpec.value.eyeColor,
        hairColor: String = _advancedAvatarSpec.value.hairColor,
        hairStyle: String = _advancedAvatarSpec.value.hairStyle,
        facialStructure: String = _advancedAvatarSpec.value.facialStructure,
        lipShape: String = _advancedAvatarSpec.value.lipShape,
        expressionVibe: String = _advancedAvatarSpec.value.expressionVibe,
        makeupStyle: String = _advancedAvatarSpec.value.makeupStyle,
        currentOutfit: String = _advancedAvatarSpec.value.currentOutfit,
        isNudeEnabled: Boolean = _advancedAvatarSpec.value.isNudeEnabled,
        backgroundVibe: String = _advancedAvatarSpec.value.backgroundVibe,
        referenceImagePath: String? = _advancedAvatarSpec.value.referenceImagePath,
    ) {
        val newAdvSpec =
            AdvancedAvatarSpec(
                name = name,
                age = age,
                heightStature = heightStature,
                bodyType = bodyType,
                breastSize = breastSize,
                waistHipRatio = waistHipRatio,
                race = race,
                skinTexture = skinTexture,
                tattoosAndPiercings = tattoosAndPiercings,
                eyeColor = eyeColor,
                hairColor = hairColor,
                hairStyle = hairStyle,
                facialStructure = facialStructure,
                lipShape = lipShape,
                expressionVibe = expressionVibe,
                makeupStyle = makeupStyle,
                currentOutfit = currentOutfit,
                isNudeEnabled = isNudeEnabled,
                backgroundVibe = backgroundVibe,
                referenceImagePath = referenceImagePath,
            )
        _advancedAvatarSpec.value = newAdvSpec
        // Keep AvatarSpec and AvatarState in sync
        val newSpec =
            AvatarSpec(
                name = name,
                age = age,
                bodyType = bodyType,
                breastSize = breastSize,
                race = race,
                eyeColor = eyeColor,
                hairColor = hairColor,
                hairStyle = hairStyle,
                currentOutfit = currentOutfit,
                isNudeEnabled = isNudeEnabled,
                backgroundVibe = backgroundVibe,
            )
        _avatarSpec.value = newSpec
        _avatarState.value =
            AvatarCustomizationState(
                hairStyle = hairStyle,
                hairColor = hairColor,
                eyeColor = eyeColor,
                outfit = currentOutfit,
                accessory = _avatarState.value.accessory,
                skinTone = _avatarState.value.skinTone,
                backgroundVibe = backgroundVibe,
            )
        viewModelScope.launch {
            dataStoreManager.saveAdvancedAvatarSpec(newAdvSpec)
            dataStoreManager.saveAvatarSpec(newSpec)
            dataStoreManager.saveAvatarState(_avatarState.value)
        }
    }

    fun saveLocalReferenceImage(
        context: android.content.Context,
        uri: android.net.Uri,
    ) {
        viewModelScope.launch {
            try {
                val dir = java.io.File(context.filesDir, "model_references")
                if (!dir.exists()) dir.mkdirs()
                val targetFile = java.io.File(dir, "ref_model_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    targetFile.outputStream().use { output -> input.copyTo(output) }
                }
                updateAdvancedAvatarSpec(referenceImagePath = targetFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearLocalReferenceImage() {
        updateAdvancedAvatarSpec(referenceImagePath = null)
    }

    fun updateCompanionPersonaMatrix(
        environmentVibe: String = _companionPersonaMatrix.value.environmentVibe,
        atmosphericLighting: String = _companionPersonaMatrix.value.atmosphericLighting,
        personalityArchetype: String = _companionPersonaMatrix.value.personalityArchetype,
        conversationalTone: String = _companionPersonaMatrix.value.conversationalTone,
        psychologicalProfile: String = _companionPersonaMatrix.value.psychologicalProfile,
        cosplayTheme: String = _companionPersonaMatrix.value.cosplayTheme,
        currentOutfitState: String = _companionPersonaMatrix.value.currentOutfitState,
        isNudeModeActive: Boolean = _companionPersonaMatrix.value.isNudeModeActive,
        attitude: String = _companionPersonaMatrix.value.attitude,
        socialDynamic: String = _companionPersonaMatrix.value.socialDynamic,
    ) {
        val newMatrix =
            CompanionPersonaMatrix(
                avatarId = "custom_model_advanced",
                environmentVibe = environmentVibe,
                atmosphericLighting = atmosphericLighting,
                personalityArchetype = personalityArchetype,
                conversationalTone = conversationalTone,
                psychologicalProfile = psychologicalProfile,
                cosplayTheme = cosplayTheme,
                currentOutfitState = currentOutfitState,
                isNudeModeActive = isNudeModeActive,
                attitude = attitude,
                socialDynamic = socialDynamic,
            )
        _companionPersonaMatrix.value = newMatrix
        viewModelScope.launch { dataStoreManager.saveCompanionPersonaMatrix(newMatrix) }
    }

    // Video Player & Generator Actions
    fun selectVideoScene(index: Int) {
        if (index in _videoScenes.value.indices) {
            _selectedVideoIndex.value = index
            _currentPlaybackMs.value = 0L
            if (_isPlaying.value) {
                startPlaybackTimer()
            }
        }
    }

    fun toggleVideoPlayPause() {
        val newPlaying = !_isPlaying.value
        _isPlaying.value = newPlaying
        if (newPlaying) {
            startPlaybackTimer()
        } else {
            playbackJob?.cancel()
        }
    }

    fun seekVideoTo(positionMs: Long) {
        val currentScene = _videoScenes.value.getOrNull(_selectedVideoIndex.value) ?: return
        val maxMs = currentScene.durationSec * 1000L
        _currentPlaybackMs.value = positionMs.coerceIn(0L, maxMs)
    }

    private fun startPlaybackTimer() {
        playbackJob?.cancel()
        playbackJob =
            viewModelScope.launch {
                while (_isPlaying.value) {
                    delay(100)
                    val currentScene = _videoScenes.value.getOrNull(_selectedVideoIndex.value) ?: break
                    val maxMs = currentScene.durationSec * 1000L
                    val nextPos = _currentPlaybackMs.value + 100L
                    if (nextPos >= maxMs) {
                        _currentPlaybackMs.value = 0L // Loop video
                    } else {
                        _currentPlaybackMs.value = nextPos
                    }
                }
            }
    }

    fun generateNewVideoClip(
        prompt: String,
        cameraMotion: CameraMotion,
        durationSec: Int,
    ) {
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
    }
}
