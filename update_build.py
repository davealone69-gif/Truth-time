import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

plugin_target = "  alias(libs.plugins.ksp)"
plugin_replace = "  alias(libs.plugins.ksp)\n  alias(libs.plugins.kotlin.serialization)"

deps_target = "  implementation(\"androidx.work:work-runtime-ktx:2.10.0\")"
deps_replace = """  implementation("androidx.work:work-runtime-ktx:2.10.0")
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.serialization)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.okhttp)
  implementation(libs.coil.compose)
"""

if plugin_target in content:
    content = content.replace(plugin_target, plugin_replace)

if deps_target in content:
    content = content.replace(deps_target, deps_replace)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated app/build.gradle.kts")

