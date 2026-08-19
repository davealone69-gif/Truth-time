plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

android {
  lint { abortOnError = false }
  namespace = "com.example"
  compileSdk = 35
  
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  
  kotlinOptions { jvmTarget = "17" }

  defaultConfig {
    applicationId = "com.example"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    val rootKeystore = rootProject.file("debug.keystore")
    val appKeystore = file("debug.keystore")
    val activeKeystore =
        when {
          rootKeystore.exists() -> rootKeystore
          appKeystore.exists() -> appKeystore
          else -> null
        }
    if (activeKeystore != null) {
      getByName("debug") {
        storeFile = activeKeystore
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
      }
      create("debugConfig") {
        storeFile = activeKeystore
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
      }
    }

    create("release") {
      val keyPath = System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH") as String?
      if (keyPath != null) {
        storeFile = rootProject.file(keyPath)
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String?
        keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String?
        keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String?
      } else {
        val rootKeystore = rootProject.file("debug.keystore")
        val appKeystore = file("debug.keystore")
        val activeKeystore = when {
          rootKeystore.exists() -> rootKeystore
          appKeystore.exists() -> appKeystore
          else -> null
        }
        if (activeKeystore != null) {
          storeFile = activeKeystore
          storePassword = "android"
          keyAlias = "androiddebugkey"
          keyPassword = "android"
        }
      }
    }
    
    // Explicitly configure debug signing to avoid checkDebugAarMetadata issues
    getByName("debug") {
      storeFile = rootProject.file("app/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      val debugConfig = signingConfigs.findByName("debugConfig")
      if (debugConfig?.storeFile != null) {
        signingConfig = debugConfig
      }
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  implementation(project(":libs"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)


  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)
  implementation("androidx.navigation:navigation-compose:2.8.7")
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.work.runtime.ktx)
  implementation("androidx.room:room-ktx:2.6.1")
  implementation("androidx.room:room-runtime:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")
  implementation("androidx.work:work-runtime-ktx:2.10.0")





  

  // Hilt / Dagger are missing from toml, using raw for what's already there if needed,
  // but the original cat showed it wasn't there? Wait, the previous cat showed it didn't have Hilt.
  // The codebase has Hilt though! Let's check if the build fails, if so I'll add them.

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
