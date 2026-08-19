import sys

filepath = 'app/src/main/java/com/example/data/DataStoreManager.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "companion object {"
replacement = """companion object {
    private val API_KEY = stringPreferencesKey("gemini_api_key")"""

if target in content and "gemini_api_key" not in content:
    content = content.replace(target, replacement)
    
    target2 = "val localAvatarFlow: Flow<LocalAvatarEntity?> ="
    replacement2 = """val apiKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[API_KEY] ?: ""
    }
    
    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    val localAvatarFlow: Flow<LocalAvatarEntity?> ="""
    content = content.replace(target2, replacement2)
    
    with open(filepath, 'w') as f:
        f.write(content)
    print("Updated DataStoreManager")
else:
    print("Already updated or target not found")
