import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """    create("release") {
      val keyPath =
          System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String?
      if (keyPath == null) {
        throw GradleException(
            "KEYSTORE_PATH is missing. Check your gradle.properties or environment variables.")
      }
      storeFile = rootProject.file(keyPath)
      storePassword =
          System.getenv("KEYSTORE_PASSWORD")
              ?: project.findProperty("KEYSTORE_PASSWORD") as String?
              ?: throw GradleException("KEYSTORE_PASSWORD is missing")
      keyAlias =
          System.getenv("KEY_ALIAS")
              ?: project.findProperty("KEY_ALIAS") as String?
              ?: throw GradleException("KEY_ALIAS is missing")
      keyPassword =
          System.getenv("KEY_PASSWORD")
              ?: project.findProperty("KEY_PASSWORD") as String?
              ?: throw GradleException("KEY_PASSWORD is missing")
    }"""

replacement = """    create("release") {
      val keyPath = System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String?
      if (keyPath != null) {
        storeFile = rootProject.file(keyPath)
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String?
        keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String?
        keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String?
      }
    }"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed!")
else:
    print("Target not found. Let's do a more robust regex or just rebuild the signing config.")

