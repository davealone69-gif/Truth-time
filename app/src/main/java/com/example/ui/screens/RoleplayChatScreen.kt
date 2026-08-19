package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.ChatMessage
import com.example.data.models.MessageSender
import com.example.data.models.PersonaModel
import com.example.ui.theme.AiBubbleColor
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.UserBubbleColor
import com.example.viewmodel.AuraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleplayChatScreen(
    viewModel: AuraViewModel,
    onNavigateToPersonas: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val messages by viewModel.messages.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()
    val typingStatusText by viewModel.typingStatusText.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to latest message
    LaunchedEffect(messages.size, isAiTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Chat Top Bar
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier =
                            Modifier.size(44.dp)
                                .clip(CircleShape)
                                .background(Color(activePersona.primaryColorHex)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = activePersona.name.take(1),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activePersona.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("chat_persona_name"),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier =
                                    Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E5FF)),
                            )
                        }
                        Text(
                            text = activePersona.tagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onNavigateToPersonas,
                        modifier = Modifier.testTag("switch_persona_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Persona",
                            tint = PurpleAccent,
                        )
                    }

                    IconButton(
                        onClick = { viewModel.clearChatHistory() },
                        modifier = Modifier.testTag("clear_chat_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // DataStore Persistence Banner
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = PurpleAccent,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DataStore Active • Conversation auto-saved for next session",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        // Scrollable Message List
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(message = msg, persona = activePersona)
            }

            if (isAiTyping) {
                item {
                    AiTypingIndicator(statusText = typingStatusText, personaName = activePersona.name)
                }
            }
        }

        // Quick Suggestion Prompts
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("How do I look?", "Show me your outfit!", "Record a video for me!").forEach {
                    chipText ->
                SuggestionChip(
                    onClick = { inputText = chipText },
                    label = { Text(chipText, fontSize = 12.sp) },
                )
            }
        }

        // Message Input Row
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Chat with ${activePersona.name}...") },
                    modifier = Modifier.weight(1f).testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        ),
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).testTag("chat_send_button"),
                    containerColor = PurpleAccent,
                    contentColor = Color.Black,
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send Message")
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    persona: PersonaModel,
) {
    val isUser = message.sender == MessageSender.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) UserBubbleColor else AiBubbleColor
    val textColor = Color.White
    val timeFormatted =
        remember(message.timestamp) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
        }

    Column(
        modifier = Modifier.fillMaxWidth().testTag("chat_bubble_${message.id}"),
        horizontalAlignment = alignment,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        ) {
            if (!isUser) {
                Text(
                    text = persona.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleAccent,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF00E5FF),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            shape =
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp,
                ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (message.imageUrl != null) {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Generated Image",
                        modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 8.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                if (message.videoUrl != null) {
                    // Placeholder for video playback
                    Box(
                        modifier =
                            Modifier.fillMaxWidth().height(
                                200.dp,
                            ).padding(bottom = 8.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Video Generated: ${message.videoUrl.take(15)}...", color = Color.White)
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
fun AiTypingIndicator(
    statusText: String,
    personaName: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = PurpleAccent,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$personaName: $statusText",
            style = MaterialTheme.typography.labelMedium,
            color = PurpleAccent,
        )
    }
}
