import re

path = "app/src/main/java/com/example/viewmodel/AuraViewModel.kt"
with open(path, "r") as f:
    content = f.read()

import_statement = """import android.graphics.Bitmap
import com.example.api.generateAvatarImage
"""
content = content.replace("import kotlinx.coroutines.flow.MutableStateFlow\n", import_statement + "import kotlinx.coroutines.flow.MutableStateFlow\n")

if "val generatedImage" not in content:
    state_decl = """
    private val _generatedImage = MutableStateFlow<Bitmap?>(null)
    val generatedImage = _generatedImage.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun generateAvatar() {
        val spec = _advancedAvatarSpec.value
        val prompt = "A highly detailed, 8k resolution, cinematic 3D character portrait in a cyberpunk style. " +
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
"""
    # Insert it right before init block
    content = content.replace("    init {", state_decl + "\n    init {")

with open(path, "w") as f:
    f.write(content)
