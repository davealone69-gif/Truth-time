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

  defaultConfig {
    applicationId = "com.example"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // IMPORTANT: Do NOT customize the "debug" signingConfig.
  // AGP ships a working default that uses ~/.android/debug.keystore
  // (auto-created). Any storeFile pointing at a missing path breaks CI.
  signingConfigs {
    create("release") {
      val keyPath =
          System.getenv("KEYSTORE_PATH")
              ?: (project.findProperty("KEYSTORE_PATH") as String?)
      if (!keyPath.isNullOrBlank()) {
        val keystoreFile = rootProject.file(keyPath)
        if (keystoreFile.exists()) {
          storeFile = keystoreFile
          storePassword =
              System.getenv("KEYSTORE_PASSWORD")
                  ?: (project.findProperty("KEYSTORE_PASSWORD") as String?)
          keyAlias =
              System.getenv("KEY_ALIAS")
                  ?: (project.findProperty("KEY_ALIAS") as String?)
          keyPassword =
              System.getenv("KEY_PASSWORD")
                  ?: (project.findProperty("KEY_PASSWORD") as String?)
        }
      }
    }
  }

  buildTypes {
    getByName("debug") {
      // Leave signingConfig unset → AGP default debug keystore
      signingConfig = null
    }
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro",
      )
      // Only attach release signing when a real keystore was configured
      val releaseCfg = signingConfigs.findByName("release")
      if (releaseCfg?.storeFile != null && releaseCfg.storeFile!!.exists()) {
        signingConfig = releaseCfg
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

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)
  implementation(libs.androidx.navigation.compose)

  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)

  implementation(libs.androidx.work.runtime.ktx)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
