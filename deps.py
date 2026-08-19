import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)
  implementation("androidx.navigation:navigation-compose:2.8.7")"""

replacement = """  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons.extended)
  implementation("androidx.navigation:navigation-compose:2.8.7")
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.work.runtime.ktx)"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed deps missing room/work.")
else:
    print("Target not found.")

