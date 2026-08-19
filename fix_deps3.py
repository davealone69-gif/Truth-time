import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  implementation(libs.androidx.datastore.preferences)"""
replacement = ""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Removed failing datastore dependency.")
else:
    print("Target not found.")

