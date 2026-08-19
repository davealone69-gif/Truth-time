import sys

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

target = """  namespace = "com.example"
  compileSdk = 35"""

replacement = """  namespace = "com.example"
  compileSdk = 35
  
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  
  kotlinOptions { jvmTarget = "17" }"""

if target in content:
    with open(filepath, 'w') as f:
        f.write(content.replace(target, replacement))
    print("Fixed compileOptions location!")
else:
    print("Target not found.")

