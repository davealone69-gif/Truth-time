import sys

filepath = 'app/src/main/java/com/example/ui/screens/RoleplayChatScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

import_target = "import androidx.compose.ui.platform.testTag"
import_replace = """import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale"""

if import_target in content and "coil.compose" not in content:
    content = content.replace(import_target, import_replace)

ui_target = """              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor)
              }"""
ui_replace = """              Column(modifier = Modifier.padding(14.dp)) {
                if (message.imageUrl != null) {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Generated Image",
                        modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 8.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                if (message.videoUrl != null) {
                    // Placeholder for video playback
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 8.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
                        Text("Video Generated: ${message.videoUrl.take(15)}...", color = Color.White)
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(48.dp))
                    }
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor)
              }"""

if ui_target in content:
    content = content.replace(ui_target, ui_replace)
    
    with open(filepath, 'w') as f:
        f.write(content)
    print("Updated RoleplayChatScreen")
else:
    print("Target not found")
