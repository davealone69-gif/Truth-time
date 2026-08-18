# Aura Studio

A production-ready multi-module Android monorepo.

## Project Metadata
- **Project name**: Aura Studio
- **ApplicationId / package**: com.example
- **Compile SDK**: 35
- **Min SDK**: 24
- **Target SDK**: 35
- **Kotlin version**: 2.0.21
- **Compose enabled**: true
- **Version code**: 1
- **Version name**: 1.0

## Setup Instructions

1. **Environment Setup**
   Copy the example properties files:
   ```bash
   cp gradle.properties.example gradle.properties
   cp local.properties.example local.properties
   ```
   Fill in your local values.

2. **Generate Keystore**
   If you do not have a keystore, run:
   ```bash
   ./scripts/generate-keystore.sh
   ```
   Then, update the `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD` in your `gradle.properties` or environment variables.

3. **Building the App**
   To build both the APK and AAB for release:
   ```bash
   ./gradlew :app:assembleRelease :app:bundleRelease
   ```

4. **Verification**
   Verify the signed output:
   ```bash
   apksigner verify app/build/outputs/apk/release/app-release.apk
   jarsigner -verify app/build/outputs/bundle/release/app-release.aab
   ```
