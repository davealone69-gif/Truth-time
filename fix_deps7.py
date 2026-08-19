import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  androidTestImplementation(platform("androidx.compose:compose-bom:2025.02.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")"""

replacement = """  androidTestImplementation("androidx.compose.ui:ui-test-junit4")"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed.")
else:
    print("Target not found.")

