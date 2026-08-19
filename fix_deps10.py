import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-extended")"""
replacement = """  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed.")
else:
    print("Target not found.")

