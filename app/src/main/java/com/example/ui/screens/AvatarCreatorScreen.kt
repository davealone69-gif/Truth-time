package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AuraViewModel

private val AccentPurple = Color(0xFF904EDD)
private val BgDeep = Color(0xFF0B0B10)
private val PanelDark = Color(0xFF12121A)

@Composable
fun AvatarCreatorScreen(
    viewModel: AuraViewModel,
    onNavigateToVideo: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val spec by viewModel.advancedAvatarSpec.collectAsState()
    val image by viewModel.generatedImage.collectAsState()
    val generating by viewModel.isGenerating.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize().background(BgDeep),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("AVATAR DESIGN", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("CREATE YOUR IDENTITY", color = AccentPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = PanelDark)) {
                Box(Modifier.fillMaxWidth().aspectRatio(0.82f), contentAlignment = Alignment.Center) {
                    if (image != null) {
                        Image(image!!.asImageBitmap(), "Generated avatar", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else if (generating) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AccentPurple)
                            Spacer(Modifier.height(10.dp))
                            Text("Generating avatar…", color = Color.White)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("YOUR AVATAR", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Configure the model below, then generate.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item { GenerationRequirements(apiConfigured = apiKey.isNotBlank()) }
        item {
            SectionCard("Identity") {
                SpecText("Name", spec.name)
                SpecText("Age", spec.age.toString())
                SpecText("Style", spec.race)
            }
        }
        item {
            SectionCard("Appearance") {
                SpecText("Hair", "${spec.hairStyle} • ${spec.hairColor}")
                SpecText("Eyes", spec.eyeColor)
                SpecText("Face", spec.facialStructure)
                SpecText("Makeup", spec.makeupStyle)
                SpecText("Expression", spec.expressionVibe)
            }
        }
        item {
            SectionCard("Scene") {
                SpecText("Outfit", spec.currentOutfit)
                SpecText("Background", spec.backgroundVibe)
                Text("Age", style = MaterialTheme.typography.labelMedium, color = Color.LightGray)
                Slider(
                    value = spec.age.toFloat(),
                    onValueChange = { viewModel.updateAdvancedAvatarSpec(age = it.toInt()) },
                    valueRange = 18f..80f,
                    colors = SliderDefaults.colors(thumbColor = AccentPurple, activeTrackColor = AccentPurple),
                    modifier = Modifier.testTag("avatar_age_slider"),
                )
            }
        }
        item {
            Button(
                onClick = { viewModel.generateAvatar() },
                enabled = !generating,
                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("generate_avatar_button"),
                shape = RoundedCornerShape(14.dp),
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (generating) "GENERATING…" else "GENERATE AVATAR")
            }
        }
        item {
            OutlinedButton(
                onClick = onNavigateToVideo,
                enabled = image != null,
                modifier = Modifier.fillMaxWidth().testTag("avatar_to_video_button"),
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("OPEN VIDEO MAKER")
            }
        }
    }
}

@Composable
private fun GenerationRequirements(apiConfigured: Boolean) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PanelDark)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = AccentPurple)
                Spacer(Modifier.width(8.dp))
                Text("What the app needs", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Requirement("Avatar specification", true)
            Requirement("Configured image-generation provider", apiConfigured)
            Requirement("Internet access for cloud generation", apiConfigured)
            Text(
                "The designer and avatar specification are local and persisted. Actual image generation currently uses the configured image provider. The copied OpenAI-compatible LLM layer is available for provider-agnostic swarm/LLM work.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
            )
        }
    }
}

@Composable
private fun Requirement(label: String, ready: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (ready) Color(0xFF52D273) else Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PanelDark)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title.uppercase(), color = AccentPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SpecText(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, maxLines = 1)
    }
}
