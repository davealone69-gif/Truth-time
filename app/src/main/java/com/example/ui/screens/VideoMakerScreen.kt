package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.models.CameraMotion
import com.example.data.models.VideoScene
import com.example.ui.theme.PurpleAccent
import com.example.viewmodel.AuraViewModel
import java.util.Locale

@Composable
fun VideoMakerScreen(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier,
) {
    val videoScenes by viewModel.videoScenes.collectAsState()
    val selectedIndex by viewModel.selectedVideoIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPlaybackMs by viewModel.currentPlaybackMs.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val avatarState by viewModel.avatarState.collectAsState()

    val currentScene =
        videoScenes.getOrNull(selectedIndex)
            ?: VideoScene(title = "Default Clip", prompt = "Character smiling", durationSec = 10)

    var promptInput by remember { mutableStateOf("") }
    var selectedMotion by remember { mutableStateOf(CameraMotion.ZOOM) }
    var selectedDuration by remember { mutableStateOf(10) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        // Title Header
        Text(
            text = "Video Content Maker",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("video_maker_title"),
        )
        Text(
            text = "Preview AI-generated character clips with full camera controls",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Video Preview Canvas
        VideoPreviewPlayerCard(
            scene = currentScene,
            isPlaying = isPlaying,
            currentMs = currentPlaybackMs,
            hairColor = avatarState.hairColor,
            outfit = avatarState.outfit,
            onTogglePlay = { viewModel.toggleVideoPlayPause() },
            onSeek = { positionMs -> viewModel.seekVideoTo(positionMs) },
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Scene Library Selector
        Text(
            text = "Generated Clip Library",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(videoScenes) { index, scene ->
                val isSelected = index == selectedIndex
                Card(
                    modifier =
                        Modifier.width(160.dp)
                            .height(90.dp)
                            .clickable { viewModel.selectVideoScene(index) }
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) PurpleAccent else Color.Transparent,
                                shape = RoundedCornerShape(12.dp),
                            ),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier =
                                Modifier.fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                MaterialTheme.colorScheme.secondary.copy(
                                                    alpha = 0.5f,
                                                ),
                                            ),
                                        ),
                                    ),
                        )
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = PurpleAccent,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "${scene.durationSec}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                )
                            }
                            Text(
                                text = scene.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                maxLines = 2,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Prompt & Scene Generation Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = PurpleAccent,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AGENT_VIDEO Clip Synthesizer",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = {
                        Text("E.g. Character walking through sunset balcony in luxury silk gown...")
                    },
                    modifier = Modifier.fillMaxWidth().testTag("video_prompt_input"),
                    shape = RoundedCornerShape(12.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Camera Motion Vector",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CameraMotion.values().toList()) { motion ->
                        val isSelected = motion == selectedMotion
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMotion = motion },
                            label = { Text(motion.displayName) },
                            modifier = Modifier.testTag("camera_motion_${motion.name.lowercase()}"),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Clip Duration (Seconds)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15, 20).forEach { dur ->
                        val isSelected = dur == selectedDuration
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDuration = dur },
                            label = { Text("${dur}s") },
                            modifier = Modifier.testTag("duration_chip_$dur"),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            viewModel.generateNewVideoClip(
                                prompt = promptInput,
                                cameraMotion = selectedMotion,
                                durationSec = selectedDuration,
                            )
                            promptInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("generate_video_button"),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Video Clip")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun VideoPreviewPlayerCard(
    scene: VideoScene,
    isPlaying: Boolean,
    currentMs: Long,
    hairColor: String,
    outfit: String,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
) {
    val totalMs = (scene.durationSec * 1000L).coerceAtLeast(1000L)
    val progressFraction = (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth().height(320.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Simulated Video Frame Animation Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val progress = progressFraction

                // Camera Motion Calculations
                val motionOffsetX =
                    when (scene.cameraMotion) {
                        CameraMotion.PAN -> (progress * 60f) - 30f
                        CameraMotion.DOLLY -> (progress * 40f) - 20f
                        else -> 0f
                    }
                val motionOffsetY =
                    when (scene.cameraMotion) {
                        CameraMotion.TILT -> (progress * 40f) - 20f
                        else -> 0f
                    }
                val motionScale =
                    when (scene.cameraMotion) {
                        CameraMotion.ZOOM -> 1f + (progress * 0.25f)
                        else -> 1f
                    }

                // Video Stage Background Gradient
                drawRect(
                    brush =
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF8E24AA), Color(0xFF1A1A2E), Color.Black),
                            center = Offset(width / 2f + motionOffsetX, height / 2f + motionOffsetY),
                            radius = (width * 0.8f) * motionScale,
                        ),
                )

                // Simulated Character Silhouette / Frame element
                val charCenterX = (width / 2f) + motionOffsetX
                val charCenterY = (height / 2f) + motionOffsetY

                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.3f),
                    center = Offset(charCenterX, charCenterY - 40f),
                    radius = 90f * motionScale,
                )

                // Character Head
                drawCircle(
                    color = Color(0xFFFFE0BD),
                    center = Offset(charCenterX, charCenterY - 30f),
                    radius = 50f * motionScale,
                )

                // Character Eyes
                drawCircle(
                    color = Color(0xFF00E5FF),
                    center = Offset(charCenterX - 15f * motionScale, charCenterY - 35f),
                    radius = 6f * motionScale,
                )
                drawCircle(
                    color = Color(0xFF00E5FF),
                    center = Offset(charCenterX + 15f * motionScale, charCenterY - 35f),
                    radius = 6f * motionScale,
                )

                // Hair Glow
                drawCircle(
                    color = Color(0xFFFF80AB).copy(alpha = 0.7f),
                    center = Offset(charCenterX, charCenterY - 65f * motionScale),
                    radius = 45f * motionScale,
                )
            }

            // Overlay Details
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Top Tag Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier =
                            Modifier.clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier =
                                Modifier.size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) Color.Red else Color.Gray),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "PREVIEWING" else "PAUSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }

                    Box(
                        modifier =
                            Modifier.clip(RoundedCornerShape(8.dp))
                                .background(PurpleAccent.copy(alpha = 0.85f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = scene.cameraMotion.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                        )
                    }
                }

                // Bottom Controls Overlay
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(12.dp),
                ) {
                    Text(
                        text = scene.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Progress Slider
                    Slider(
                        value = currentMs.toFloat(),
                        onValueChange = { onSeek(it.toLong()) },
                        valueRange = 0f..totalMs.toFloat(),
                        modifier = Modifier.fillMaxWidth().testTag("video_progress_slider"),
                        colors =
                            SliderDefaults.colors(
                                thumbColor = PurpleAccent,
                                activeTrackColor = PurpleAccent,
                                inactiveTrackColor = Color.Gray.copy(alpha = 0.5f),
                            ),
                    )

                    // Playback Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = formatTime(currentMs) + " / " + formatTime(totalMs),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onSeek((currentMs - 3000L).coerceAtLeast(0L)) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay5,
                                    contentDescription = "Rewind 5s",
                                    tint = Color.White,
                                )
                            }

                            IconButton(
                                onClick = onTogglePlay,
                                modifier =
                                    Modifier.size(44.dp)
                                        .clip(CircleShape)
                                        .background(PurpleAccent)
                                        .testTag("video_play_pause_button"),
                            ) {
                                Icon(
                                    imageVector =
                                        if (isPlaying) {
                                            Icons.Default.Pause
                                        } else {
                                            Icons.Default.PlayArrow
                                        },
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                )
                            }

                            IconButton(
                                onClick = { onSeek((currentMs + 3000L).coerceAtMost(totalMs)) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forward5,
                                    contentDescription = "Forward 5s",
                                    tint = Color.White,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSec = ms / 1000L
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", min, sec)
}
