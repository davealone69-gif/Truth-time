import sys

filepath = 'app/src/main/java/com/example/ui/screens/PersonasScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "// Swarm Health & Architecture Monitor Card"
replacement = """
        item {
          val apiKey by viewModel.apiKey.collectAsState()
          var isEditingApiKey by remember { mutableStateOf(false) }
          var tempApiKey by remember { mutableStateOf(apiKey) }
          
          Card(
              modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gemini API Configuration", style = MaterialTheme.typography.titleMedium, color = GoldAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditingApiKey) {
                        OutlinedTextField(
                            value = tempApiKey,
                            onValueChange = { tempApiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth()
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
                        Text("Current Key: $maskedKey", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
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

        // Swarm Health & Architecture Monitor Card"""

if target in content and "Gemini API Configuration" not in content:
    content = content.replace(target, replacement)
    
    with open(filepath, 'w') as f:
        f.write(content)
    print("Updated PersonasScreen")
else:
    print("Already updated or target not found")
