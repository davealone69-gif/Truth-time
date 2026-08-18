package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.AvatarGraphicPreview
import com.example.viewmodel.AuraViewModel

@Composable
fun AvatarCreatorScreen(viewModel: AuraViewModel, modifier: Modifier = Modifier) {
  val avatarState by viewModel.avatarState.collectAsState()
  val avatarSpec by viewModel.avatarSpec.collectAsState()
  val advSpec by viewModel.advancedAvatarSpec.collectAsState()
  val activePersona by viewModel.activePersona.collectAsState()

  val context = LocalContext.current
  val photoPickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.saveLocalReferenceImage(context, it) }
      }

  val heightOptions =
      listOf(
          "Tall / Model (5'9\"+)",
          "Petite (5'2\"-5'4\")",
          "Average Height (5'5\"-5'7\")",
          "Amazonian (6'0\"+)")
  val bodyTypes =
      listOf("Hourglass", "Slim / Petite", "Curvy / Athletic", "Voluptuous", "Muscular / Fit")
  val breastSizes =
      listOf("Large / Natural", "Small / Subtle", "Medium / Natural", "Full / Voluptuous")
  val waistHipRatios =
      listOf("Extreme Hourglass", "Classic 0.7 Ratio", "Slim & Straight", "Athletic Curved")
  val races =
      listOf("Eurasian", "Caucasian", "Latina", "East Asian", "Afro-Caribbean", "Middle Eastern")

  val skinTextures =
      listOf(
          "Natural Realism (with subtle freckles/texture)",
          "Smooth Studio Porcelain",
          "Sun-Kissed / Tan Lines",
          "Dewy Glow")
  val tattoosOptions =
      listOf(
          "Minimalist Ink & Piercings",
          "Full Sleeves & Nose Ring",
          "No Tattoos / Clean Skin",
          "Subtle Rib & Ankle Ink")
  val facialStructures = listOf("High Cheekbones", "Soft Oval", "Sculpted Jawline", "Heart Shaped")
  val lipShapes = listOf("Plump & Defined", "Natural Rose", "Pouty Full", "Classic Curved")
  val expressions =
      listOf(
          "Seductive / Smoldering",
          "Playful & Mischievous",
          "Mysterious",
          "Innocent & Sweet",
          "Dominant & Fierce")
  val makeupStyles =
      listOf(
          "Smoky Eyes", "Minimal Natural", "Bold Red Lip", "Gothic Glam", "High-Fashion Editorial")

  val hairColors =
      listOf(
          "Jet Black",
          "Platinum Blonde",
          "Deep Brunette",
          "Vibrant Red",
          "Silver Grey",
          "Pastel Pink")
  val hairStyles =
      listOf(
          "Long Sleek Straight",
          "Long Cascading Waves",
          "Sleek Bob",
          "Curled Ponytail",
          "Cyber Pixie")
  val outfits =
      listOf(
          "Designer Silk Lingerie",
          "Casual Streetwear",
          "Evening Gala Dress",
          "Swimwear",
          "Cosplay / Thematic")
  val bgVibes =
      listOf(
          "Moody Studio Backdrop with Cinematic Key Lighting",
          "Luxury Penthouse Suite",
          "Sunset Penthouse",
          "Neon Cyberpunk Lounge",
          "Cozy Coffee Shop")

  Column(
      modifier =
          modifier
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.background)
              .verticalScroll(rememberScrollState())
              .padding(16.dp)) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Column {
                Text(
                    text = "Model Design Studio",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("avatar_creator_title"))
                Text(
                    text = "Advanced AvatarSpec & Prompt Template Wrapper",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              IconButton(
                  onClick = {
                    viewModel.updateAdvancedAvatarSpec(
                        heightStature = heightOptions.random(),
                        bodyType = bodyTypes.random(),
                        breastSize = breastSizes.random(),
                        waistHipRatio = waistHipRatios.random(),
                        race = races.random(),
                        skinTexture = skinTextures.random(),
                        facialStructure = facialStructures.random(),
                        lipShape = lipShapes.random(),
                        expressionVibe = expressions.random(),
                        makeupStyle = makeupStyles.random(),
                        hairColor = hairColors.random(),
                        hairStyle = hairStyles.random(),
                        currentOutfit = outfits.random(),
                        backgroundVibe = bgVibes.random(),
                        age = (18..45).random())
                  },
                  modifier = Modifier.testTag("randomize_avatar_button")) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Randomize Avatar",
                        tint = MaterialTheme.colorScheme.primary)
                  }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Companion Name Field
        OutlinedTextField(
            value = advSpec.name,
            onValueChange = { newName -> viewModel.updateAdvancedAvatarSpec(name = newName) },
            label = { Text("Companion Name") },
            modifier = Modifier.fillMaxWidth().testTag("companion_name_input"),
            singleLine = true)

        Spacer(modifier = Modifier.height(12.dp))

        // Age Slider
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Age",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "${advSpec.age} years old",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary)
              }
          Slider(
              value = advSpec.age.toFloat(),
              onValueChange = { newAge ->
                viewModel.updateAdvancedAvatarSpec(age = newAge.toInt())
              },
              valueRange = 18f..45f,
              steps = 26,
              modifier = Modifier.fillMaxWidth().testTag("age_slider"))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Graphic Preview Component
        AvatarGraphicPreview(
            state = avatarState, characterName = advSpec.name.ifEmpty { activePersona.name })

        Spacer(modifier = Modifier.height(20.dp))

        // Prompt Template Wrapper Strategy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (advSpec.isNudeEnabled)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        else MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Prompt Template Wrapper Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text =
                                if (advSpec.isNudeEnabled)
                                    "NO CLOTHES TOGGLE: Active (Fine art classical lighting, unadorned anatomical study, high-end studio photography)"
                                else "Standard clothing translation: ${advSpec.currentOutfit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                      }
                      Switch(
                          checked = advSpec.isNudeEnabled,
                          onCheckedChange = { enabled ->
                            viewModel.updateAdvancedAvatarSpec(isNudeEnabled = enabled)
                          },
                          modifier = Modifier.testTag("nude_mode_toggle"))
                    }
              }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Local Reference Image Stash & Byte Routing Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(8.dp),
                          modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Reference Image",
                                tint = MaterialTheme.colorScheme.primary)
                            Column {
                              Text(
                                  text = "Local Reference Photo Stash",
                                  style = MaterialTheme.typography.titleSmall,
                                  color = MaterialTheme.colorScheme.onSurface)
                              Text(
                                  text =
                                      if (!advSpec.referenceImagePath.isNullOrEmpty())
                                          "Stashed in local app directory: ${advSpec.referenceImagePath?.substringAfterLast("/")}"
                                      else
                                          "No reference photo stashed (Click button to pick from device)",
                                  style = MaterialTheme.typography.bodySmall,
                                  color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                          }
                    }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()) {
                      Button(
                          onClick = { photoPickerLauncher.launch("image/*") },
                          modifier = Modifier.weight(1f).testTag("upload_reference_photo_button"),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primary)) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pick Photo",
                                modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Reference Image")
                          }

                      if (!advSpec.referenceImagePath.isNullOrEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.clearLocalReferenceImage() },
                            modifier = Modifier.testTag("clear_reference_photo_button")) {
                              Icon(
                                  imageVector = Icons.Default.Delete,
                                  contentDescription = "Clear",
                                  modifier = Modifier.size(18.dp))
                            }
                      }
                    }
              }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Abstracted Payloads Routing Token Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(12.dp)) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      Icon(
                          imageVector = Icons.Default.Lock,
                          contentDescription = "Abstracted Payload Token",
                          tint = MaterialTheme.colorScheme.tertiary)
                      Text(
                          text = "Abstracted Model Matrix Token Payload",
                          style = MaterialTheme.typography.titleSmall,
                          color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text =
                        "Abstracted metadata token sent to generation pipeline without exposing raw restricted pixel data:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()) {
                      Text(
                          text = advSpec.toAbstractedPromptToken(),
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.primary,
                          modifier =
                              Modifier.padding(10.dp).testTag("abstracted_payload_token_text"))
                    }
              }
            }

        Spacer(modifier = Modifier.height(20.dp))

        // Attribute Selection Sections
        AttributeCategorySection(
            title = "Height & Stature",
            options = heightOptions,
            selected = advSpec.heightStature,
            onSelect = { viewModel.updateAdvancedAvatarSpec(heightStature = it) },
            testTagPrefix = "height_stature")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Body Type",
            options = bodyTypes,
            selected = advSpec.bodyType,
            onSelect = { viewModel.updateAdvancedAvatarSpec(bodyType = it) },
            testTagPrefix = "body_type")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Breast Size",
            options = breastSizes,
            selected = advSpec.breastSize,
            onSelect = { viewModel.updateAdvancedAvatarSpec(breastSize = it) },
            testTagPrefix = "breast_size")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Waist-to-Hip Ratio",
            options = waistHipRatios,
            selected = advSpec.waistHipRatio,
            onSelect = { viewModel.updateAdvancedAvatarSpec(waistHipRatio = it) },
            testTagPrefix = "waist_hip_ratio")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Ethnicity / Race",
            options = races,
            selected = advSpec.race,
            onSelect = { viewModel.updateAdvancedAvatarSpec(race = it) },
            testTagPrefix = "race")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Skin Texture & Realism",
            options = skinTextures,
            selected = advSpec.skinTexture,
            onSelect = { viewModel.updateAdvancedAvatarSpec(skinTexture = it) },
            testTagPrefix = "skin_texture")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Tattoos & Piercings",
            options = tattoosOptions,
            selected = advSpec.tattoosAndPiercings.firstOrNull() ?: tattoosOptions[0],
            onSelect = { viewModel.updateAdvancedAvatarSpec(tattoosAndPiercings = listOf(it)) },
            testTagPrefix = "tattoos_piercings")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Facial Structure",
            options = facialStructures,
            selected = advSpec.facialStructure,
            onSelect = { viewModel.updateAdvancedAvatarSpec(facialStructure = it) },
            testTagPrefix = "facial_structure")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Lip Shape & Lips",
            options = lipShapes,
            selected = advSpec.lipShape,
            onSelect = { viewModel.updateAdvancedAvatarSpec(lipShape = it) },
            testTagPrefix = "lip_shape")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Expression Vibe",
            options = expressions,
            selected = advSpec.expressionVibe,
            onSelect = { viewModel.updateAdvancedAvatarSpec(expressionVibe = it) },
            testTagPrefix = "expression_vibe")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Makeup Style",
            options = makeupStyles,
            selected = advSpec.makeupStyle,
            onSelect = { viewModel.updateAdvancedAvatarSpec(makeupStyle = it) },
            testTagPrefix = "makeup_style")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Hair Style",
            options = hairStyles,
            selected = advSpec.hairStyle,
            onSelect = { viewModel.updateAdvancedAvatarSpec(hairStyle = it) },
            testTagPrefix = "hair_style")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Hair Color",
            options = hairColors,
            selected = advSpec.hairColor,
            onSelect = { viewModel.updateAdvancedAvatarSpec(hairColor = it) },
            testTagPrefix = "hair_color")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Wardrobe Selection",
            options = outfits,
            selected = advSpec.currentOutfit,
            onSelect = { viewModel.updateAdvancedAvatarSpec(currentOutfit = it) },
            testTagPrefix = "outfit")

        Spacer(modifier = Modifier.height(16.dp))

        AttributeCategorySection(
            title = "Background Studio Lighting",
            options = bgVibes,
            selected = advSpec.backgroundVibe,
            onSelect = { viewModel.updateAdvancedAvatarSpec(backgroundVibe = it) },
            testTagPrefix = "bg_vibe")

        Spacer(modifier = Modifier.height(24.dp))

        // Save & Generate Model Action Button
        Button(
            onClick = { viewModel.updateAdvancedAvatarSpec() },
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("generate_model_save_button"),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Generate Model & Save to Studio", style = MaterialTheme.typography.titleMedium)
            }

        Spacer(modifier = Modifier.height(16.dp))

        // DataStore Sync Footer Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)) {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                          text = "AdvancedAvatarSpec Persistent State Active",
                          style = MaterialTheme.typography.titleSmall,
                          color = MaterialTheme.colorScheme.onSurface)
                      Text(
                          text =
                              "All physical, skin, and prompt parameters are stored in DataStore for seamless prompt translation.",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                  }
            }

        Spacer(modifier = Modifier.height(32.dp))
      }
}

@Composable
private fun AttributeCategorySection(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    testTagPrefix: String
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onBackground)
          Text(
              text = selected,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary)
        }

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)) {
          items(options) { item ->
            val isSelected = item == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(item) },
                label = { Text(item) },
                leadingIcon =
                    if (isSelected) {
                      {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp))
                      }
                    } else null,
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier =
                    Modifier.testTag("${testTagPrefix}_chip_${item.lowercase().replace(" ", "_")}"))
          }
        }
  }
}
