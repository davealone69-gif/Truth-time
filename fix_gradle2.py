import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """    create("release") {
      val keyPath = System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String?
      if (keyPath != null) {
        storeFile = rootProject.file(keyPath)
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String?
        keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String?
        keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String?
      }
    }"""

replacement = """    create("release") {
      val keyPath = System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String?
      if (keyPath != null) {
        storeFile = rootProject.file(keyPath)
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String?
        keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String?
        keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String?
      }
    }
    
    // Explicitly configure debug signing to avoid checkDebugAarMetadata issues
    getByName("debug") {
      storeFile = file("debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed!")
else:
    print("Target not found. Let's do a more robust regex or just rebuild the signing config.")

