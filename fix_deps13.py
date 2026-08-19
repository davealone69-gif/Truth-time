import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  androidTestImplementation(libs.androidx.ui.test.junit4)
  androidTestImplementation(platform(libs.androidx.compose.bom))"""
replacement = """  androidTestImplementation(libs.androidx.ui.test.junit4)"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed.")
else:
    print("Target not found.")

