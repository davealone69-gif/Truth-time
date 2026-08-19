import sys

filepath = 'app/src/main/AndroidManifest.xml'
with open(filepath, 'r') as f:
    content = f.read()

target = """  <application"""
replacement = """  <application"""
addition = """    <provider
        android:name="androidx.startup.InitializationProvider"
        android:authorities="${applicationId}.androidx-startup"
        android:exported="false"
        tools:node="merge">
        <!-- If you are using androidx.startup to initialize other components -->
        <meta-data
            android:name="androidx.work.WorkManagerInitializer"
            android:value="androidx.startup"
            tools:node="remove" />
    </provider>"""

if "WorkManagerInitializer" not in content:
    with open(filepath, 'w') as f:
        # insert right inside application tag
        parts = content.split("</application>")
        new_content = parts[0] + addition + "\n  </application>" + parts[1]
        f.write(new_content)
    print("Fixed manifest.")
else:
    print("Target not found or already added.")

