package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.data.GeminiNativeClient
import com.example.ui.screens.*
import com.example.ui.theme.AuraStudioTheme
import com.example.viewmodel.AuraViewModel

enum class NavigationTab(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    CHAT("chat", "Chat", Icons.Default.Chat, "nav_tab_chat"),
    AVATAR("avatar", "Avatar", Icons.Default.Face, "nav_tab_avatar"),
    VIDEO("video", "Video Maker", Icons.Default.Videocam, "nav_tab_video"),
    PERSONAS("personas", "Swarm & Personas", Icons.Default.Psychology, "nav_tab_personas"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileTopBar(currentRoute: String, onNavigate: (String) -> Unit, onSettings: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val current = NavigationTab.values().firstOrNull { it.route == currentRoute } ?: NavigationTab.CHAT
    TopAppBar(
        title = { Text(current.title) },
        navigationIcon = { Icon(current.icon, contentDescription = current.title, modifier = Modifier.padding(start = 12.dp)) },
        actions = {
            Box {
                IconButton(onClick = { expanded = true }) { Icon(Icons.Default.Menu, contentDescription = "Main menu") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    NavigationTab.values().forEach { tab ->
                        DropdownMenuItem(text = { Text(tab.title) }, leadingIcon = { Icon(tab.icon, null) }, onClick = { expanded = false; onNavigate(tab.route) })
                    }
                    HorizontalDivider()
                    DropdownMenuItem(text = { Text("Settings") }, leadingIcon = { Icon(Icons.Default.Settings, null) }, onClick = { expanded = false; onSettings() })
                }
            }
        },
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraStudioTheme {
                val viewModel: AuraViewModel = viewModel()
                val navController = rememberNavController()
                val entry by navController.currentBackStackEntryAsState()
                val route = entry?.destination?.route ?: NavigationTab.CHAT.route
                var showSettings by remember { mutableStateOf(false) }
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                LaunchedEffect(Unit) { GeminiNativeClient.API_KEY = prefs.getString("gemini_api_key", "") ?: "" }

                if (showSettings) {
                    var key by remember { mutableStateOf(GeminiNativeClient.API_KEY) }
                    Dialog(onDismissRequest = { showSettings = false }) {
                        Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Column(Modifier.padding(20.dp)) {
                                Text("AI Provider", style = MaterialTheme.typography.headlineSmall)
                                Text("Gemini is optional. The OpenAI-compatible LLM layer is available for provider-agnostic swarm work.", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text("Gemini API Key") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(16.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = { showSettings = false }) { Text("Cancel") }
                                    Button(onClick = { GeminiNativeClient.API_KEY = key; prefs.edit().putString("gemini_api_key", key).apply(); showSettings = false }) { Text("Save") }
                                }
                            }
                        }
                    }
                }

                Scaffold(topBar = { MobileTopBar(route, { target -> navController.navigate(target) { launchSingleTop = true; restoreState = true } }, { showSettings = true }) }) { padding ->
                    NavHost(navController, NavigationTab.CHAT.route, Modifier.padding(padding)) {
                        composable(NavigationTab.CHAT.route) { RoleplayChatScreen(viewModel, { navController.navigate(NavigationTab.PERSONAS.route) }) }
                        composable(NavigationTab.AVATAR.route) { AvatarCreatorScreen(viewModel) { navController.navigate(NavigationTab.VIDEO.route) } }
                        composable(NavigationTab.VIDEO.route) { VideoMakerScreen(viewModel) }
                        composable(NavigationTab.PERSONAS.route) { PersonasScreen(viewModel) { navController.navigate(NavigationTab.CHAT.route) } }
                    }
                }
            }
        }
    }
}
