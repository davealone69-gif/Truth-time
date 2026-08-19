import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = "ksp(libs.androidx.room.compiler)"
replacement = ""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Removed failing KSP definition.")
else:
    print("Target not found.")

