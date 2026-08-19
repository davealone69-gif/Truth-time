package com.example.data.models

data class AdvancedAvatarSpec(
    val id: String = "custom_model_advanced",
    val name: String = "Custom Elite Model",
    // Core Physical
    val age: Int = 23,
    val heightStature: String = "Tall / Model (5'9\"+)",
    val bodyType: String = "Hourglass",
    val breastSize: String = "Large / Natural",
    val waistHipRatio: String = "Extreme Hourglass",
    val race: String = "Eurasian",
    // Detailed Skin & Features
    val skinTexture: String = "Natural Realism (with subtle freckles/texture)",
    val tattoosAndPiercings: List<String> = listOf("Navel/Nose/Ear Piercings", "Minimalist Ink"),
    // Head, Face & Glam
    val eyeColor: String = "Amber Gold",
    val hairColor: String = "Jet Black",
    val hairStyle: String = "Long Sleek Straight",
    val facialStructure: String = "High Cheekbones",
    val lipShape: String = "Plump & Defined",
    val expressionVibe: String = "Seductive / Smoldering",
    val makeupStyle: String = "Smoky Eyes",
    // Wardrobe & Environment
    val currentOutfit: String = "Designer Silk Lingerie",
    val isNudeEnabled: Boolean = false,
    val backgroundVibe: String = "Moody Studio Backdrop with Cinematic Key Lighting",
    // Local Byte Reference Image Path
    val referenceImagePath: String? = null,
) {
    fun toAbstractedPromptToken(): String {
        val clothingToken =
            if (isNudeEnabled) {
                "[ANATOMICAL_STUDY_CLASSICAL_LIGHTING_FINE_ART]"
            } else {
                "[OUTFIT:$currentOutfit]"
            }
        val tattoosToken =
            if (tattoosAndPiercings.isNotEmpty()) {
                "[INK:${tattoosAndPiercings.joinToString(",")}]"
            } else {
                "[INK:NONE]"
            }
        val refImgToken =
            if (!referenceImagePath.isNullOrEmpty()) "[LOCAL_REF_STASH_ACTIVE]" else "[REF:DEFAULT]"
        return "$refImgToken [MODEL_SPEC:$name|AGE:$age|STATURE:$heightStature|BODY:$bodyType|BUST:$breastSize|WAIST_HIP:$waistHipRatio|RACE:$race|SKIN:$skinTexture|FACE:$facialStructure|LIPS:$lipShape|EXPR:$expressionVibe|MAKEUP:$makeupStyle|HAIR:$hairStyle,$hairColor|$clothingToken|BG:$backgroundVibe]"
    }
}
