import sys

filepath = 'app/src/main/AndroidManifest.xml'
with open(filepath, 'r') as f:
    content = f.read()

target = "<application"
replacement = "<uses-permission android:name=\"android.permission.INTERNET\" />\n    <application"

if "android.permission.INTERNET" not in content and target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Added internet permission.")
else:
    print("Already has internet permission or target not found.")

