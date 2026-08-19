import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  implementation(libs.androidx.room.ktx)"""
replacement = ""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Removed failing Room ktx dependency.")
else:
    print("Target not found.")

