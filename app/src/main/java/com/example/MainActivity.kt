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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.GeminiNativeClient
import com.example.ui.screens.AvatarCreatorScreen
import com.example.ui.screens.PersonasScreen
import com.example.ui.screens.RoleplayChatScreen
import com.example.ui.screens.VideoMakerScreen
import com.example.ui.theme.AuraStudioTheme
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AuraViewModel

enum class NavigationTab(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String,
) {
    CHAT("chat", "CHAT", Icons.Default.Chat, "nav_tab_chat"),
    AVATAR("avatar", "APPEARANCE", Icons.Default.Face, "nav_tab_avatar"),
    VIDEO("video", "STUDIO", Icons.Default.Videocam, "nav_tab_video"),
    PERSONAS("personas", "PERSONAS", Icons.Default.Psychology, "nav_tab_personas"),
}

@Composable
fun CyberpunkSideMenu(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight().width(96.dp).background(DarkCanvas).padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Text("M", color = PurpleAccent, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        NavigationTab.values().forEach { tab ->
            val selected = currentRoute == tab.route
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNavigate(tab.route) },
                contentAlignment = Alignment.CenterStart,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier.fillMaxWidth().matchParentSize().padding(end = 8.dp)
                            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                            .background(Color(0xFF1B1A26)),
                    )
                    Box(modifier = Modifier.width(4.dp).matchParentSize().background(PurpleAccent))
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(tab.icon, contentDescription = tab.title, tint = if (selected) TextPrimary else TextSecondary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tab.title, color = if (selected) TextPrimary else TextSecondary, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraStudioTheme {
                val auraViewModel: AuraViewModel = viewModel()
                val apiKey by auraViewModel.apiKey.collectAsState()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: NavigationTab.CHAT.route
                var showSettings by remember { mutableStateOf(false) }

                LaunchedEffect(apiKey) {
                    GeminiNativeClient.API_KEY = apiKey
                }

                if (showSettings) {
                    var tempKey by remember(apiKey) { mutableStateOf(apiKey) }
                    Dialog(onDismissRequest = { showSettings = false }) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Settings", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = tempKey,
                                    onValueChange = { tempKey = it },
                                    label = { Text("Gemini API Key") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (tempKey.isBlank()) "Local mode: chat, avatar preview and studio remain usable without a key." else "Gemini live mode enabled.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = { showSettings = false }) { Text("Cancel") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = {
                                        auraViewModel.setApiKey(tempKey)
                                        GeminiNativeClient.API_KEY = tempKey
                                        showSettings = false
                                    }) { Text("Save") }
                                }
                            }
                        }
                    }
                }

                Surface(color = DarkCanvas, modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        CyberpunkSideMenu(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            onSettingsClick = { showSettings = true },
                        )

                        VerticalDivider()

                        Scaffold(
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.background,
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = NavigationTab.CHAT.route,
                                modifier = Modifier.padding(innerPadding),
                            ) {
                                composable(NavigationTab.CHAT.route) {
                                    RoleplayChatScreen(
                                        viewModel = auraViewModel,
                                        onNavigateToPersonas = { navController.navigate(NavigationTab.PERSONAS.route) },
                                    )
                                }
                                composable(NavigationTab.AVATAR.route) { AvatarCreatorScreen(viewModel = auraViewModel) }
                                composable(NavigationTab.VIDEO.route) { VideoMakerScreen(viewModel = auraViewModel) }
                                composable(NavigationTab.PERSONAS.route) {
                                    PersonasScreen(
                                        viewModel = auraViewModel,
                                        onSelectAndStartChat = {
                                            navController.navigate(NavigationTab.CHAT.route) {
                                                popUpTo(NavigationTab.CHAT.route) { inclusive = true }
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
