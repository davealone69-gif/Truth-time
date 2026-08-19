import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.work.runtime.ktx)"""

replacement = """  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.work.runtime.ktx)
  implementation("androidx.room:room-ktx:2.6.1")
  implementation("androidx.room:room-runtime:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")
  implementation("androidx.work:work-runtime-ktx:2.10.0")"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed room/work version.")
else:
    print("Target not found.")

