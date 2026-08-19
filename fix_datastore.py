import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  implementation(libs.androidx.core.ktx)"""
replacement = """  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed.")
else:
    print("Target not found.")

