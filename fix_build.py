import re

path = "app/build.gradle.kts"
with open(path, "r") as f:
    content = f.read()

lines = content.split('\n')
for i, line in enumerate(lines):
    if "buildConfigField" in line and "GEMINI_API_KEY" in line:
        lines[i] = '        val apiKey = project.findProperty("GEMINI_API_KEY") as? String ?: ""\n        buildConfigField("String", "GEMINI_API_KEY", "\\"$apiKey\\"")'
content = '\n'.join(lines)

with open(path, "w") as f:
    f.write(content)
