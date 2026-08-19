plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.kover) apply false
}

allprojects {
  // apply(plugin = "io.gitlab.arturbosch.detekt")
  apply(plugin = "com.diffplug.spotless")
  apply(plugin = "org.jetbrains.kotlinx.kover")

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      ktfmt()
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktfmt()
    }
  }
}
