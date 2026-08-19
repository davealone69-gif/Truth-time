import sys

filepath = 'app/src/main/java/com/example/MainActivity.kt'
with open(filepath, 'r') as f:
    content = f.read()

import_target = "import com.example.viewmodel.AuraViewModel"
import_replacement = """import com.example.viewmodel.AuraViewModel
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import android.content.Context
import com.example.data.GeminiNativeClient"""
content = content.replace(import_target, import_replacement)

scaffold_target = """        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {"""
scaffold_replacement = """        var showSettings by remember { mutableStateOf(false) }
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        LaunchedEffect(Unit) {
            GeminiNativeClient.API_KEY = sharedPrefs.getString("gemini_api_key", "") ?: ""
        }

        if (showSettings) {
            var tempKey by remember { mutableStateOf(GeminiNativeClient.API_KEY) }
            Dialog(onDismissRequest = { showSettings = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Settings", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = tempKey,
                            onValueChange = { tempKey = it },
                            label = { Text("Gemini API Key") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showSettings = false }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                GeminiNativeClient.API_KEY = tempKey
                                sharedPrefs.edit().putString("gemini_api_key", tempKey).apply()
                                showSettings = false
                            }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Aura Studio", color = GoldAccent) },
                    actions = {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = GoldAccent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {"""
content = content.replace(scaffold_target, scaffold_replacement)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated MainActivity")
