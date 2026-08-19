import sys

filepath = 'gradle.properties'
with open(filepath, 'r') as f:
    content = f.read()

target = "org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m"
replacement = "org.gradle.jvmargs=-Xmx3g -XX:MaxMetaspaceSize=1g"

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed memory.")
else:
    print("Target not found.")

