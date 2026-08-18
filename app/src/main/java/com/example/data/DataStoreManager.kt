package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.models.AdvancedAvatarSpec
import com.example.data.models.AvatarCustomizationState
import com.example.data.models.AvatarSpec
import com.example.data.models.ChatMessage
import com.example.data.models.CompanionPersonaMatrix
import com.example.data.models.MessageSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by
    preferencesDataStore(name = "aura_studio_prefs")

class DataStoreManager(private val context: Context) {

  companion object {
    private val CHAT_MESSAGES_KEY = stringPreferencesKey("chat_messages_raw")
    private val ACTIVE_PERSONA_KEY = stringPreferencesKey("active_persona_id")
    private val HAIR_STYLE_KEY = stringPreferencesKey("avatar_hair_style")
    private val HAIR_COLOR_KEY = stringPreferencesKey("avatar_hair_color")
    private val EYE_COLOR_KEY = stringPreferencesKey("avatar_eye_color")
    private val OUTFIT_KEY = stringPreferencesKey("avatar_outfit")
    private val ACCESSORY_KEY = stringPreferencesKey("avatar_accessory")
    private val SKIN_TONE_KEY = stringPreferencesKey("avatar_skin_tone")
    private val BG_VIBE_KEY = stringPreferencesKey("avatar_bg_vibe")
    private val SPEC_NAME_KEY = stringPreferencesKey("avatar_spec_name")
    private val AGE_KEY = intPreferencesKey("avatar_age")
    private val BODY_TYPE_KEY = stringPreferencesKey("avatar_body_type")
    private val BREAST_SIZE_KEY = stringPreferencesKey("avatar_breast_size")
    private val RACE_KEY = stringPreferencesKey("avatar_race")
    private val IS_NUDE_ENABLED_KEY = booleanPreferencesKey("avatar_is_nude_enabled")

    // Advanced Avatar Spec Keys
    private val HEIGHT_STATURE_KEY = stringPreferencesKey("adv_height_stature")
    private val WAIST_HIP_RATIO_KEY = stringPreferencesKey("adv_waist_hip_ratio")
    private val SKIN_TEXTURE_KEY = stringPreferencesKey("adv_skin_texture")
    private val TATTOOS_PIERCINGS_KEY = stringPreferencesKey("adv_tattoos_piercings")
    private val FACIAL_STRUCTURE_KEY = stringPreferencesKey("adv_facial_structure")
    private val LIP_SHAPE_KEY = stringPreferencesKey("adv_lip_shape")
    private val EXPRESSION_VIBE_KEY = stringPreferencesKey("adv_expression_vibe")
    private val MAKEUP_STYLE_KEY = stringPreferencesKey("adv_makeup_style")
    private val REF_IMAGE_PATH_KEY = stringPreferencesKey("adv_ref_image_path")

    // Matrix Keys
    private val MATRIX_LIGHTING_KEY = stringPreferencesKey("matrix_lighting")
    private val MATRIX_ARCHETYPE_KEY = stringPreferencesKey("matrix_archetype")
    private val MATRIX_TONE_KEY = stringPreferencesKey("matrix_tone")
    private val MATRIX_PSYCH_KEY = stringPreferencesKey("matrix_psych")
    private val MATRIX_COSPLAY_KEY = stringPreferencesKey("matrix_cosplay")
    private val MATRIX_ATTITUDE_KEY = stringPreferencesKey("matrix_attitude")
    private val MATRIX_SOCIAL_KEY = stringPreferencesKey("matrix_social")
  }

  // Save chat messages to DataStore
  suspend fun saveChatHistory(messages: List<ChatMessage>) {
    val serialized =
        messages.joinToString(separator = "|||") { msg ->
          "${msg.id}:::${msg.sender.name}:::${escape(msg.text)}:::${msg.timestamp}:::${msg.personaId}"
        }
    context.dataStore.edit { preferences -> preferences[CHAT_MESSAGES_KEY] = serialized }
  }

  // Load chat messages from DataStore
  val chatHistoryFlow: Flow<List<ChatMessage>> =
      context.dataStore.data.map { preferences ->
        val serialized = preferences[CHAT_MESSAGES_KEY] ?: return@map emptyList()
        if (serialized.isEmpty()) return@map emptyList()

        try {
          serialized.split("|||").mapNotNull { part ->
            val tokens = part.split(":::")
            if (tokens.size >= 5) {
              ChatMessage(
                  id = tokens[0],
                  sender =
                      if (tokens[1] == MessageSender.USER.name) MessageSender.USER
                      else MessageSender.AI,
                  text = unescape(tokens[2]),
                  timestamp = tokens[3].toLongOrNull() ?: System.currentTimeMillis(),
                  personaId = tokens[4])
            } else null
          }
        } catch (e: Exception) {
          emptyList()
        }
      }

  // Save active persona
  suspend fun saveActivePersona(personaId: String) {
    context.dataStore.edit { prefs -> prefs[ACTIVE_PERSONA_KEY] = personaId }
  }

  val activePersonaFlow: Flow<String> =
      context.dataStore.data.map { prefs -> prefs[ACTIVE_PERSONA_KEY] ?: "crazzers_ai" }

  // Save Avatar Customization State
  suspend fun saveAvatarState(state: AvatarCustomizationState) {
    context.dataStore.edit { prefs ->
      prefs[HAIR_STYLE_KEY] = state.hairStyle
      prefs[HAIR_COLOR_KEY] = state.hairColor
      prefs[EYE_COLOR_KEY] = state.eyeColor
      prefs[OUTFIT_KEY] = state.outfit
      prefs[ACCESSORY_KEY] = state.accessory
      prefs[SKIN_TONE_KEY] = state.skinTone
      prefs[BG_VIBE_KEY] = state.backgroundVibe
    }
  }

  val avatarStateFlow: Flow<AvatarCustomizationState> =
      context.dataStore.data.map { prefs ->
        AvatarCustomizationState(
            hairStyle = prefs[HAIR_STYLE_KEY] ?: "Long Waves",
            hairColor = prefs[HAIR_COLOR_KEY] ?: "Platinum Blonde",
            eyeColor = prefs[EYE_COLOR_KEY] ?: "Emerald Green",
            outfit = prefs[OUTFIT_KEY] ?: "Luxury Silk Gown",
            accessory = prefs[ACCESSORY_KEY] ?: "Diamond Choker",
            skinTone = prefs[SKIN_TONE_KEY] ?: "Warm Porcelain",
            backgroundVibe = prefs[BG_VIBE_KEY] ?: "Sunset Penthouse")
      }

  suspend fun saveAvatarSpec(spec: AvatarSpec) {
    context.dataStore.edit { prefs ->
      prefs[SPEC_NAME_KEY] = spec.name
      prefs[AGE_KEY] = spec.age
      prefs[BODY_TYPE_KEY] = spec.bodyType
      prefs[BREAST_SIZE_KEY] = spec.breastSize
      prefs[RACE_KEY] = spec.race
      prefs[EYE_COLOR_KEY] = spec.eyeColor
      prefs[HAIR_COLOR_KEY] = spec.hairColor
      prefs[HAIR_STYLE_KEY] = spec.hairStyle
      prefs[OUTFIT_KEY] = spec.currentOutfit
      prefs[IS_NUDE_ENABLED_KEY] = spec.isNudeEnabled
      prefs[BG_VIBE_KEY] = spec.backgroundVibe
    }
  }

  val avatarSpecFlow: Flow<AvatarSpec> =
      context.dataStore.data.map { prefs ->
        AvatarSpec(
            name = prefs[SPEC_NAME_KEY] ?: "My Custom Companion",
            age = prefs[AGE_KEY] ?: 22,
            bodyType = prefs[BODY_TYPE_KEY] ?: "Curvy / Athletic",
            breastSize = prefs[BREAST_SIZE_KEY] ?: "Medium / Natural",
            race = prefs[RACE_KEY] ?: "Eurasian",
            eyeColor = prefs[EYE_COLOR_KEY] ?: "Emerald Green",
            hairColor = prefs[HAIR_COLOR_KEY] ?: "Platinum Blonde",
            hairStyle = prefs[HAIR_STYLE_KEY] ?: "Long Cascading Waves",
            currentOutfit = prefs[OUTFIT_KEY] ?: "Lingerie",
            isNudeEnabled = prefs[IS_NUDE_ENABLED_KEY] ?: false,
            backgroundVibe = prefs[BG_VIBE_KEY] ?: "Luxury Penthouse Suite")
      }

  suspend fun saveAdvancedAvatarSpec(spec: AdvancedAvatarSpec) {
    context.dataStore.edit { prefs ->
      prefs[SPEC_NAME_KEY] = spec.name
      prefs[AGE_KEY] = spec.age
      prefs[HEIGHT_STATURE_KEY] = spec.heightStature
      prefs[BODY_TYPE_KEY] = spec.bodyType
      prefs[BREAST_SIZE_KEY] = spec.breastSize
      prefs[WAIST_HIP_RATIO_KEY] = spec.waistHipRatio
      prefs[RACE_KEY] = spec.race
      prefs[SKIN_TEXTURE_KEY] = spec.skinTexture
      prefs[TATTOOS_PIERCINGS_KEY] = spec.tattoosAndPiercings.joinToString(",")
      prefs[EYE_COLOR_KEY] = spec.eyeColor
      prefs[HAIR_COLOR_KEY] = spec.hairColor
      prefs[HAIR_STYLE_KEY] = spec.hairStyle
      prefs[FACIAL_STRUCTURE_KEY] = spec.facialStructure
      prefs[LIP_SHAPE_KEY] = spec.lipShape
      prefs[EXPRESSION_VIBE_KEY] = spec.expressionVibe
      prefs[MAKEUP_STYLE_KEY] = spec.makeupStyle
      prefs[OUTFIT_KEY] = spec.currentOutfit
      prefs[IS_NUDE_ENABLED_KEY] = spec.isNudeEnabled
      prefs[BG_VIBE_KEY] = spec.backgroundVibe
      if (spec.referenceImagePath != null) {
        prefs[REF_IMAGE_PATH_KEY] = spec.referenceImagePath
      } else {
        prefs.remove(REF_IMAGE_PATH_KEY)
      }
    }
  }

  val advancedAvatarSpecFlow: Flow<AdvancedAvatarSpec> =
      context.dataStore.data.map { prefs ->
        val tpString = prefs[TATTOOS_PIERCINGS_KEY] ?: "Navel/Nose/Ear Piercings,Minimalist Ink"
        val tpList = if (tpString.isEmpty()) emptyList() else tpString.split(",")
        AdvancedAvatarSpec(
            name = prefs[SPEC_NAME_KEY] ?: "Custom Elite Model",
            age = prefs[AGE_KEY] ?: 23,
            heightStature = prefs[HEIGHT_STATURE_KEY] ?: "Tall / Model (5'9\"+)",
            bodyType = prefs[BODY_TYPE_KEY] ?: "Hourglass",
            breastSize = prefs[BREAST_SIZE_KEY] ?: "Large / Natural",
            waistHipRatio = prefs[WAIST_HIP_RATIO_KEY] ?: "Extreme Hourglass",
            race = prefs[RACE_KEY] ?: "Eurasian",
            skinTexture =
                prefs[SKIN_TEXTURE_KEY] ?: "Natural Realism (with subtle freckles/texture)",
            tattoosAndPiercings = tpList,
            eyeColor = prefs[EYE_COLOR_KEY] ?: "Amber Gold",
            hairColor = prefs[HAIR_COLOR_KEY] ?: "Jet Black",
            hairStyle = prefs[HAIR_STYLE_KEY] ?: "Long Sleek Straight",
            facialStructure = prefs[FACIAL_STRUCTURE_KEY] ?: "High Cheekbones",
            lipShape = prefs[LIP_SHAPE_KEY] ?: "Plump & Defined",
            expressionVibe = prefs[EXPRESSION_VIBE_KEY] ?: "Seductive / Smoldering",
            makeupStyle = prefs[MAKEUP_STYLE_KEY] ?: "Smoky Eyes",
            currentOutfit = prefs[OUTFIT_KEY] ?: "Designer Silk Lingerie",
            isNudeEnabled = prefs[IS_NUDE_ENABLED_KEY] ?: false,
            backgroundVibe =
                prefs[BG_VIBE_KEY] ?: "Moody Studio Backdrop with Cinematic Key Lighting",
            referenceImagePath = prefs[REF_IMAGE_PATH_KEY])
      }

  suspend fun saveCompanionPersonaMatrix(matrix: CompanionPersonaMatrix) {
    context.dataStore.edit { prefs ->
      prefs[BG_VIBE_KEY] = matrix.environmentVibe
      prefs[MATRIX_LIGHTING_KEY] = matrix.atmosphericLighting
      prefs[MATRIX_ARCHETYPE_KEY] = matrix.personalityArchetype
      prefs[MATRIX_TONE_KEY] = matrix.conversationalTone
      prefs[MATRIX_PSYCH_KEY] = matrix.psychologicalProfile
      prefs[MATRIX_COSPLAY_KEY] = matrix.cosplayTheme
      prefs[OUTFIT_KEY] = matrix.currentOutfitState
      prefs[IS_NUDE_ENABLED_KEY] = matrix.isNudeModeActive
      prefs[MATRIX_ATTITUDE_KEY] = matrix.attitude
      prefs[MATRIX_SOCIAL_KEY] = matrix.socialDynamic
    }
  }

  val companionPersonaMatrixFlow: Flow<CompanionPersonaMatrix> =
      context.dataStore.data.map { prefs ->
        CompanionPersonaMatrix(
            avatarId = "custom_model_advanced",
            environmentVibe = prefs[BG_VIBE_KEY] ?: "Cyberpunk Neon Alley / Luxury Penthouse Suite",
            atmosphericLighting =
                prefs[MATRIX_LIGHTING_KEY]
                    ?: "Moody cinematic key lighting with soft rim highlights",
            personalityArchetype = prefs[MATRIX_ARCHETYPE_KEY] ?: "Playful & Luxurious",
            conversationalTone =
                prefs[MATRIX_TONE_KEY] ?: "Witty, attentive, deeply engaging, and responsive",
            psychologicalProfile =
                prefs[MATRIX_PSYCH_KEY]
                    ?: "High empathy, tailored to user preferences, adaptive memory",
            cosplayTheme =
                prefs[MATRIX_COSPLAY_KEY]
                    ?: "Sci-Fi Cyberpunk Mercenary / Fantasy Elf Warrior / Maid Uniform",
            currentOutfitState = prefs[OUTFIT_KEY] ?: "Custom Cosplay / Lingerie",
            isNudeModeActive = prefs[IS_NUDE_ENABLED_KEY] ?: false,
            attitude =
                prefs[MATRIX_ATTITUDE_KEY]
                    ?: "Confident, unapologetically alluring, fiercely loyal, and responsive",
            socialDynamic =
                prefs[MATRIX_SOCIAL_KEY]
                    ?: "Companion-led immersion with real-time trait adjustments")
      }

  suspend fun clearChatHistory() {
    context.dataStore.edit { prefs -> prefs.remove(CHAT_MESSAGES_KEY) }
  }

  private fun escape(str: String): String = str.replace("\n", "\\n").replace(":::", "\\col")

  private fun unescape(str: String): String = str.replace("\\n", "\n").replace("\\col", ":::")
}
