package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.PersonaRepository
import com.example.data.models.PersonaModel
import com.example.ui.theme.PurpleAccent
import com.example.viewmodel.AuraViewModel

@Composable
fun PersonasScreen(
    viewModel: AuraViewModel,
    onSelectAndStartChat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activePersona by viewModel.activePersona.collectAsState()
    val swarmHealth by viewModel.swarmHealth.collectAsState()

    LazyColumn(
        modifier =
            modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(
                    text = "Aura AI Personas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("personas_screen_title"),
                )
                Text(
                    text = "Select a companion persona to activate Swarm node routing",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Persona Cards List
        items(PersonaRepository.defaultPersonas) { persona ->
            val isSelected = persona.id == activePersona.id
            PersonaCardItem(
                persona = persona,
                isSelected = isSelected,
                onSelect = {
                    viewModel.selectPersona(persona)
                    onSelectAndStartChat()
                },
            )
        }

        // Companion Persona Matrix Card
        item {
            val matrix by viewModel.companionPersonaMatrix.collectAsState()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = "Companion Persona Matrix",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Fine-tune psychological profile, tone, attitude & cosplay theme",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Personality Archetype: ${matrix.personalityArchetype}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp),
                    ) {
                        listOf(
                            "Playful & Luxurious",
                            "Seductive & Alluring",
                            "Dominant & Fierce",
                            "Affectionate",
                        )
                            .forEach { archetype ->
                                FilterChip(
                                    selected = matrix.personalityArchetype == archetype,
                                    onClick = {
                                        viewModel.updateCompanionPersonaMatrix(
                                            personalityArchetype = archetype,
                                        )
                                    },
                                    label = {
                                        Text(archetype, style = MaterialTheme.typography.labelSmall)
                                    },
                                )
                            }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cosplay & Wardrobe Theme: ${matrix.cosplayTheme}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp),
                    ) {
                        listOf(
                            "Sci-Fi Cyberpunk Mercenary",
                            "Fantasy Elf Warrior",
                            "Maid Uniform",
                            "Gothic Queen",
                        )
                            .forEach { theme ->
                                FilterChip(
                                    selected = matrix.cosplayTheme == theme,
                                    onClick = {
                                        viewModel.updateCompanionPersonaMatrix(cosplayTheme = theme)
                                    },
                                    label = {
                                        Text(theme, style = MaterialTheme.typography.labelSmall)
                                    },
                                )
                            }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Unconstrained Mode Toggle",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text =
                                    if (matrix.isNudeModeActive) {
                                        "Fine Art Classical lighting active"
                                    } else {
                                        "Standard outfit mode"
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = matrix.isNudeModeActive,
                            onCheckedChange = { active ->
                                viewModel.updateCompanionPersonaMatrix(isNudeModeActive = active)
                            },
                        )
                    }
                }
            }
        }

        item {
            val apiKey by viewModel.apiKey.collectAsState()
            var isEditingApiKey by remember { mutableStateOf(false) }
            var tempApiKey by remember { mutableStateOf(apiKey) }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gemini API Configuration", style = MaterialTheme.typography.titleMedium, color = PurpleAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditingApiKey) {
                        OutlinedTextField(
                            value = tempApiKey,
                            onValueChange = { tempApiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = {
                                viewModel.setApiKey(tempApiKey)
                                isEditingApiKey = false
                            }) { Text("Save") }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { isEditingApiKey = false }) { Text("Cancel") }
                        }
                    } else {
                        val maskedKey = if (apiKey.length > 8) apiKey.take(4) + "..." + apiKey.takeLast(4) else "Not Set"
                        Text(
                            "Current Key: $maskedKey",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = {
                            tempApiKey = apiKey
                            isEditingApiKey = true
                        }) {
                            Text("Edit API Key")
                        }
                    }
                }
            }
        }

        // Swarm Health & Architecture Monitor Card
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = PurpleAccent,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SWARM_MASTER Architecture Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

                        Box(
                            modifier =
                                Modifier.clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = swarmHealth.status.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF00E5FF),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Active Sub-Agent Nodes:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        swarmHealth.activeAgents.forEach { agent ->
                            Box(
                                modifier =
                                    Modifier.clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = agent.replace("AGENT_", ""),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleAccent,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Self-Healing Log: ${swarmHealth.healingLog}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PersonaCardItem(
    persona: PersonaModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { onSelect() }
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) PurpleAccent else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                )
                .testTag("persona_card_${persona.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier.size(52.dp)
                        .clip(CircleShape)
                        .background(Color(persona.primaryColorHex)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = persona.name.take(1),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = persona.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active Persona",
                            tint = PurpleAccent,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Text(
                    text = persona.tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleAccent,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = persona.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
        }
    }
}
