package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AvatarCreatorScreen
import com.example.ui.screens.PersonasScreen
import com.example.ui.screens.RoleplayChatScreen
import com.example.ui.screens.VideoMakerScreen
import com.example.ui.theme.AuraStudioTheme
import com.example.ui.theme.GoldAccent
import com.example.viewmodel.AuraViewModel

enum class NavigationTab(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
  CHAT("chat", "Roleplay", Icons.Default.Chat, "nav_tab_chat"),
  AVATAR("avatar", "Avatar Creator", Icons.Default.Face, "nav_tab_avatar"),
  VIDEO("video", "Video Studio", Icons.Default.Videocam, "nav_tab_video"),
  PERSONAS("personas", "Personas", Icons.Default.Psychology, "nav_tab_personas")
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AuraStudioTheme {
        val auraViewModel: AuraViewModel = viewModel()
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: NavigationTab.CHAT.route

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
              NavigationBar(
                  containerColor = MaterialTheme.colorScheme.surface,
                  contentColor = MaterialTheme.colorScheme.onSurface) {
                    NavigationTab.values().forEach { tab ->
                      val selected = currentRoute == tab.route
                      NavigationBarItem(
                          selected = selected,
                          onClick = {
                            if (currentRoute != tab.route) {
                              navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                              }
                            }
                          },
                          icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint =
                                    if (selected) GoldAccent
                                    else MaterialTheme.colorScheme.onSurfaceVariant)
                          },
                          label = {
                            Text(
                                text = tab.title,
                                color =
                                    if (selected) GoldAccent
                                    else MaterialTheme.colorScheme.onSurfaceVariant)
                          },
                          modifier = Modifier.testTag(tab.testTag))
                    }
                  }
            }) { innerPadding ->
              NavHost(
                  navController = navController,
                  startDestination = NavigationTab.CHAT.route,
                  modifier = Modifier.padding(innerPadding)) {
                    composable(NavigationTab.CHAT.route) {
                      RoleplayChatScreen(
                          viewModel = auraViewModel,
                          onNavigateToPersonas = {
                            navController.navigate(NavigationTab.PERSONAS.route)
                          })
                    }
                    composable(NavigationTab.AVATAR.route) {
                      AvatarCreatorScreen(viewModel = auraViewModel)
                    }
                    composable(NavigationTab.VIDEO.route) {
                      VideoMakerScreen(viewModel = auraViewModel)
                    }
                    composable(NavigationTab.PERSONAS.route) {
                      PersonasScreen(
                          viewModel = auraViewModel,
                          onSelectAndStartChat = {
                            navController.navigate(NavigationTab.CHAT.route) {
                              popUpTo(NavigationTab.CHAT.route) { inclusive = true }
                            }
                          })
                    }
                  }
            }
      }
    }
  }
}
