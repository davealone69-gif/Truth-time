import re

path = "app/src/main/java/com/example/ui/screens/AvatarCreatorScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("Icons.AutoMirrored.Filled.DirectionsRun", "Icons.Default.DirectionsRun")
content = content.replace("Icons.AutoMirrored.Outlined.HelpOutline", "Icons.Outlined.HelpOutline")
content = content.replace("Icons.AutoMirrored.Filled.HelpOutline", "Icons.Default.HelpOutline")
content = content.replace("Icons.AutoMirrored.Filled.Undo", "Icons.Default.Undo")
content = content.replace("Icons.AutoMirrored.Filled.Redo", "Icons.Default.Redo")
content = content.replace("Icons.AutoMirrored.Outlined.Undo", "Icons.Outlined.Undo")
content = content.replace("Icons.AutoMirrored.Outlined.Redo", "Icons.Outlined.Redo")
content = content.replace("import androidx.compose.material.icons.automirrored.filled.*", "")
content = content.replace("import androidx.compose.material.icons.automirrored.outlined.*", "")

with open(path, "w") as f:
    f.write(content)
