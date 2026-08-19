import re

path = "app/src/main/java/com/example/ui/screens/AvatarCreatorScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Replace Horizontal Dividers
content = re.sub(r'Divider\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.height\(([^)]+)\),\s*color = ([^)]+)\)',
                 r'HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = \1, color = \2)', content)

# Replace Vertical Dividers (Using Box because VerticalDivider might not exist in older compose versions, but since we are using material3 it does exist)
content = re.sub(r'Divider\(\s*modifier = Modifier\s*\.fillMaxHeight\(\)\s*\.width\(([^)]+)\),\s*color = ([^)]+)\)',
                 r'VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = \1, color = \2)', content)
content = re.sub(r'Divider\(\s*modifier = Modifier\s*\.width\(([^)]+)\)\s*\.fillMaxHeight\(\),\s*color = ([^)]+)\)',
                 r'VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = \1, color = \2)', content)

# General Divider fallback if any
content = content.replace(" Divider(", " HorizontalDivider(")

content = content.replace("Icons.Default.DirectionsRun", "Icons.AutoMirrored.Filled.DirectionsRun")
content = content.replace("Icons.Outlined.HelpOutline", "Icons.AutoMirrored.Outlined.HelpOutline")
content = content.replace("Icons.Default.HelpOutline", "Icons.AutoMirrored.Filled.HelpOutline")
content = content.replace("Icons.Filled.Undo", "Icons.AutoMirrored.Filled.Undo")
content = content.replace("Icons.Default.Undo", "Icons.AutoMirrored.Filled.Undo")
content = content.replace("Icons.Filled.Redo", "Icons.AutoMirrored.Filled.Redo")
content = content.replace("Icons.Default.Redo", "Icons.AutoMirrored.Filled.Redo")
content = content.replace("Icons.Outlined.Undo", "Icons.AutoMirrored.Outlined.Undo")
content = content.replace("Icons.Outlined.Redo", "Icons.AutoMirrored.Outlined.Redo")

with open(path, "w") as f:
    f.write(content)
