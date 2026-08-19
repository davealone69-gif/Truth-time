# Gradle Wrapper

The `gradle-wrapper.jar` is restored automatically by the `Validate Gradle Wrapper` workflow on push to `main`.

If you need it locally:

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  https://raw.githubusercontent.com/gradle/gradle/v8.10.2/gradle/wrapper/gradle-wrapper.jar
echo "2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046  gradle/wrapper/gradle-wrapper.jar" | sha256sum -c
```
