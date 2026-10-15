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
Use Android Gradle Plugin lint:
```bash
./gradlew lint
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
- Run Android Lint.
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
- GitHub Actions CI is executed by GitHub; a green CI run proves only the checks included in that run, not successful runtime use on a physical phone.


## AI capability verification

A configured provider or reachable endpoint is not proof of completed generation. Mark chat verified only after a non-empty model response, image generation only after a real decodable image, and video generation only after a real playable media asset is returned. Missing API keys must produce configuration guidance, not fabricated output or placeholder URLs. Local avatar preview does not imply that remote image/video generation works. Physical-device runtime remains a separate verification step.
