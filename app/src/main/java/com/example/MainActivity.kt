package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.data.GeminiNativeClient
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AuraViewModel

enum class NavigationTab(val route: String, val title: String, val icon: ImageVector, val testTag: String) {
    CHAT("chat", "CHAT", Icons.Default.Chat, "nav_tab_chat"),
    AVATAR("avatar", "APPEARANCE", Icons.Default.Face, "nav_tab_avatar"),
    VIDEO("video", "STUDIO", Icons.Default.Videocam, "nav_tab_video"),
    PERSONAS("personas", "PERSONAS", Icons.Default.Psychology, "nav_tab_personas"),
}

@Composable
fun CyberpunkSideMenu(currentRoute: String, onNavigate: (String) -> Unit, onSettingsClick: () -> Unit) {
    Column(Modifier.fillMaxHeight().width(96.dp).background(DarkCanvas).padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Text("M", color = PurpleAccent, fontSize = 32.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(24.dp))
        NavigationTab.values().forEach { tab ->
            val selected = currentRoute == tab.route
            Box(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNavigate(tab.route) }, contentAlignment = Alignment.CenterStart) {
                if (selected) {
                    Box(Modifier.fillMaxWidth().matchParentSize().padding(end = 8.dp).clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)).background(Color(0xFF1B1A26)))
                    Box(Modifier.width(4.dp).matchParentSize().background(PurpleAccent))
                }
                Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(tab.icon, tab.title, tint = if (selected) TextPrimary else TextSecondary, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(tab.title, color = if (selected) TextPrimary else TextSecondary, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Settings, "Settings", tint = TextSecondary) }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        GeminiNativeClient.initialize(applicationContext)
        setContent {
            AuraStudioTheme {
                val auraViewModel: AuraViewModel = viewModel()
                val apiKey by auraViewModel.apiKey.collectAsState()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: NavigationTab.CHAT.route
                var showSettings by remember { mutableStateOf(false) }
                LaunchedEffect(apiKey) { GeminiNativeClient.API_KEY = apiKey }

                if (showSettings) {
                    var tempKey by remember(apiKey) { mutableStateOf(apiKey) }
                    Dialog(onDismissRequest = { showSettings = false }) {
                        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Settings", style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(tempKey, { tempKey = it }, label = { Text("Gemini API Key") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Text(if (tempKey.isBlank()) "Local mode: chat, avatar preview and studio remain usable without a key." else "Gemini live mode enabled.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton({ showSettings = false }) { Text("Cancel") }
                                    Spacer(Modifier.width(8.dp))
                                    Button({ auraViewModel.setApiKey(tempKey); GeminiNativeClient.API_KEY = tempKey; showSettings = false }) { Text("Save") }
                                }
                            }
                        }
                    }
                }

                Surface(color = DarkCanvas, modifier = Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxSize()) {
                        CyberpunkSideMenu(currentRoute, { route -> if (currentRoute != route) navController.navigate(route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } }, { showSettings = true })
                        VerticalDivider()
                        Scaffold(Modifier.weight(1f), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                            NavHost(navController, NavigationTab.CHAT.route, Modifier.padding(innerPadding)) {
                                composable(NavigationTab.CHAT.route) { RoleplayChatScreen(auraViewModel) { navController.navigate(NavigationTab.PERSONAS.route) } }
                                composable(NavigationTab.AVATAR.route) { AvatarCreatorScreen(auraViewModel) }
                                composable(NavigationTab.VIDEO.route) { VideoMakerScreen(auraViewModel) }
                                composable(NavigationTab.PERSONAS.route) { PersonasScreen(auraViewModel) { navController.navigate(NavigationTab.CHAT.route) { popUpTo(NavigationTab.CHAT.route) { inclusive = true } } } }
                            }
                        }
                    }
                }
            }
        }
    }
}
