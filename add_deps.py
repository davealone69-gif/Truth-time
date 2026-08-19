import sys

filepath = 'gradle/libs.versions.toml'
with open(filepath, 'r') as f:
    content = f.read()

versions_addition = """
retrofit = "2.11.0"
okhttp = "4.12.0"
serialization = "1.6.3"
coil = "2.6.0"
"""

libraries_addition = """
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "serialization" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
"""

plugins_addition = """
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
"""

# add to versions
content = content.replace("[versions]", "[versions]" + versions_addition)
content = content.replace("[libraries]", "[libraries]" + libraries_addition)
content = content.replace("[plugins]", "[plugins]" + plugins_addition)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated libs.versions.toml")

