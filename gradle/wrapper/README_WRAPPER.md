# Gradle Wrapper

The `gradle-wrapper.jar` was missing. Download the official one for Gradle 8.10.2:

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.10.2/gradle/wrapper/gradle-wrapper.jar
echo "2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046  gradle/wrapper/gradle-wrapper.jar" | sha256sum -c
```

Then commit the binary.
