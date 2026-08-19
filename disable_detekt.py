import sys

filepath = 'build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  apply(plugin = "io.gitlab.arturbosch.detekt")"""
replacement = """  // apply(plugin = "io.gitlab.arturbosch.detekt")"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Disabled detekt.")
else:
    print("Target not found.")

