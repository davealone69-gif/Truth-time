import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  androidTestImplementation("androidx.compose.ui:ui-test-junit4")"""
replacement = """  androidTestImplementation(libs.androidx.ui.test.junit4)
  androidTestImplementation(platform(libs.androidx.compose.bom))"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed.")
else:
    print("Target not found.")

