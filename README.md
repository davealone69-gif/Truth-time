# Truth-time

A multi-module Android monorepo.

## Project Identity
- **Project name**: Truth-time
- **ApplicationId / package**: com.aurastudio.app
- **Compile SDK**: 34
- **Min SDK**: 26
- **Target SDK**: 34
- **Kotlin version**: 2.0.21
- **Compose enabled**: true

## Modules
- `:app` - Main application module
- `:libs` - Shared libraries module
- `buildSrc` - Build logic

## Setup Instructions

1. **Environment Setup**
   Copy the example properties files to configure locally:
   ```bash
   cp gradle.properties.example gradle.properties
   cp local.properties.example local.properties
   ```
   Provide valid keystore credentials in `gradle.properties` if building release manually without CI.

## Build and Verification

### Local Build Commands
Debug build (verified):
```bash
./gradlew :app:assembleDebug
```

Release build (verified):
```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

### Static Analysis
Linting is fully enabled via detekt and ktlint (verified):
```bash
./gradlew detekt ktlintCheck
```

### Tests
Run unit tests (verified):
```bash
./gradlew test
```
- Tests implemented: `AuraViewModelTest`, `LocalStudioViewModelTest`, `GeminiNativeClientTest`.
- Tests run correctly via Robolectric with in-memory Room and Coroutine testing APIs.

## CI Behavior
GitHub Actions is configured for pull requests and main branch pushes.
The CI will:
- Check out the repository.
- Validate the Gradle Wrapper (using `gradle/actions/wrapper-validation@v6`).
- Set up JDK 17 and Gradle.
- Run Detekt and Ktlint.
- Run unit tests.
- Build Debug APK.
- Build Release APK and AAB (for tagged releases) using GitHub Actions secrets.
- Upload artifacts.

## Artifact Locations
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`

## Known Limitations
- Runtime verification on a real device or emulator was NOT VERIFIED due to the lack of an available emulator in the build environment.
- CI/CD workflow was NOT EXECUTED since this environment does not run GitHub Actions.
