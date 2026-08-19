plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.kover) apply false
}

// Keep root lightweight. Apply formatting/coverage only where useful and only when
// the plugins are intentionally enabled. Forcing them on allprojects breaks CI
// when formatting tools are not fully configured.
subprojects {
  // Optional: enable Spotless later with a dedicated convention plugin.
}
