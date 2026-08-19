package com.example.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object GeminiNativeClient {
    // Note: Since we cannot modify Gradle to inject BuildConfig secrets,
    // you will need to provide your API key here or via a UI input.
    var API_KEY: String = ""

    suspend fun generateText(
        prompt: String,
        systemInstruction: String? = null,
    ): String? =
        withContext(Dispatchers.IO) {
            if (API_KEY.isBlank()) return@withContext "Please configure your Gemini API Key in the settings."
            try {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=\$API_KEY")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val requestJson = JSONObject()
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", prompt)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                requestJson.put("contents", contentsArray)

                if (systemInstruction != null) {
                    val sysInstObj = JSONObject()
                    val sysPartsArray = JSONArray()
                    val sysPartObj = JSONObject()
                    sysPartObj.put("text", systemInstruction)
                    sysPartsArray.put(sysPartObj)
                    sysInstObj.put("parts", sysPartsArray)
                    requestJson.put("systemInstruction", sysInstObj)
                }

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(requestJson.toString())
                writer.flush()
                writer.close()

                if (connection.responseCode == 200) {
                    val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val responseJson = JSONObject(responseStr)
                    val candidates = responseJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text")
                        }
                    }
                } else {
                    Log.e("GeminiNativeClient", "Error: \${connection.responseCode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "API Call Failed: \${e.message}"
            }
            return@withContext null
        }

    suspend fun generateImage(prompt: String): Bitmap? =
        withContext(Dispatchers.IO) {
            if (API_KEY.isBlank()) return@withContext null
            try {
                val url =
                    URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=\$API_KEY")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.readTimeout = 60000
                connection.connectTimeout = 60000

                val requestJson = JSONObject()
                val contentsArray = JSONArray()
                val contentObj = JSONObject()
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", prompt)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
                requestJson.put("contents", contentsArray)

                val generationConfig = JSONObject()
                val imageConfig = JSONObject()
                imageConfig.put("aspectRatio", "1:1")
                imageConfig.put("imageSize", "1K")
                generationConfig.put("imageConfig", imageConfig)

                val responseModalities = JSONArray()
                responseModalities.put("IMAGE")
                generationConfig.put("responseModalities", responseModalities)
                requestJson.put("generationConfig", generationConfig)

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(requestJson.toString())
                writer.flush()
                writer.close()

                if (connection.responseCode == 200) {
                    val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val responseJson = JSONObject(responseStr)
                    val candidates = responseJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val parts = candidates.getJSONObject(0).optJSONObject("content")?.optJSONArray("parts")
                        if (parts != null) {
                            for (i in 0 until parts.length()) {
                                val part = parts.getJSONObject(i)
                                if (part.has("inlineData")) {
                                    val inlineData = part.getJSONObject("inlineData")
                                    val data = inlineData.optString("data")
                                    val bytes = Base64.decode(data, Base64.DEFAULT)
                                    return@withContext BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                }
                            }
                        }
                    }
                } else {
                    Log.e(
                        "GeminiNativeClient",
                        "Error: \${connection.responseCode} \${connection.errorStream.bufferedReader().use { it.readText() }}",
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }

    suspend fun generateVideo(prompt: String): String? =
        withContext(Dispatchers.IO) {
            if (API_KEY.isBlank()) return@withContext null
            try {
                val url =
                    URL(
                        "https://generativelanguage.googleapis.com/v1beta/models/veo-3.1-fast-generate-preview:generateVideos?key=\$API_KEY",
                    )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.readTimeout = 60000
                connection.connectTimeout = 60000

                val requestJson = JSONObject()
                requestJson.put("prompt", prompt)

                val configObj = JSONObject()
                configObj.put("numberOfVideos", 1)
                configObj.put("resolution", "1080p")
                configObj.put("aspectRatio", "16:9")
                requestJson.put("config", configObj)

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(requestJson.toString())
                writer.flush()
                writer.close()

                if (connection.responseCode == 200) {
                    val responseStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val responseJson = JSONObject(responseStr)
                    return@withContext responseJson.optString("name")
                } else {
                    Log.e(
                        "GeminiNativeClient",
                        "Error: \${connection.responseCode} \${connection.errorStream.bufferedReader().use { it.readText() }}",
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }
}
