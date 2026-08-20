package com.example.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object GeminiNativeClient {
    var API_KEY: String = BuildConfig.GEMINI_API_KEY
        set(value) {
            field = value.trim()
        }

    private const val TEXT_MODEL = "gemini-2.5-flash"
    private const val IMAGE_MODEL = "gemini-2.5-flash-image"

    suspend fun generateText(
        prompt: String,
        systemInstruction: String? = null,
    ): String = withContext(Dispatchers.IO) {
        if (API_KEY.isBlank()) return@withContext localTextFallback(prompt)

        try {
            val connection = openConnection(
                "https://generativelanguage.googleapis.com/v1beta/models/$TEXT_MODEL:generateContent?key=$API_KEY",
            )
            val request = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }))
                if (!systemInstruction.isNullOrBlank()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                    })
                }
            }

            val response = postJson(connection, request)
            if (connection.responseCode in 200..299) {
                val candidates = response.optJSONArray("candidates")
                val parts = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text").orEmpty().trim()
                if (text.isNotEmpty()) return@withContext text
            } else {
                Log.e("GeminiNativeClient", "Text API ${connection.responseCode}: ${response.optString("error")}")
            }
        } catch (error: Exception) {
            Log.e("GeminiNativeClient", "Text generation failed", error)
        }

        localTextFallback(prompt)
    }

    suspend fun generateImage(prompt: String): Bitmap = withContext(Dispatchers.IO) {
        if (API_KEY.isBlank()) return@withContext createLocalImage(prompt)

        try {
            val connection = openConnection(
                "https://generativelanguage.googleapis.com/v1beta/models/$IMAGE_MODEL:generateContent?key=$API_KEY",
                timeoutMs = 60_000,
            )
            val request = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("IMAGE"))
                    put("imageConfig", JSONObject().put("aspectRatio", "1:1"))
                })
            }

            val response = postJson(connection, request)
            if (connection.responseCode in 200..299) {
                val parts = response.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                for (index in 0 until (parts?.length() ?: 0)) {
                    val data = parts?.optJSONObject(index)?.optJSONObject("inlineData")?.optString("data")
                    if (!data.isNullOrBlank()) {
                        val bytes = Base64.decode(data, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let { return@withContext it }
                    }
                }
            } else {
                Log.e("GeminiNativeClient", "Image API ${connection.responseCode}: ${response.optString("error")}")
            }
        } catch (error: Exception) {
            Log.e("GeminiNativeClient", "Image generation failed", error)
        }

        createLocalImage(prompt)
    }

    suspend fun generateVideo(prompt: String): String = withContext(Dispatchers.IO) {
        if (API_KEY.isBlank()) return@withContext "local-preview://${System.currentTimeMillis()}"

        try {
            val connection = openConnection(
                "https://generativelanguage.googleapis.com/v1beta/models/veo-3.1-fast-generate-preview:generateVideos?key=$API_KEY",
                timeoutMs = 60_000,
            )
            val request = JSONObject().apply {
                put("prompt", prompt)
                put("config", JSONObject().apply {
                    put("numberOfVideos", 1)
                    put("resolution", "720p")
                    put("aspectRatio", "16:9")
                })
            }
            val response = postJson(connection, request)
            if (connection.responseCode in 200..299) {
                response.optString("name").takeIf { it.isNotBlank() }?.let { return@withContext it }
            } else {
                Log.e("GeminiNativeClient", "Video API ${connection.responseCode}: ${response.optString("error")}")
            }
        } catch (error: Exception) {
            Log.e("GeminiNativeClient", "Video generation failed", error)
        }

        "local-preview://${System.currentTimeMillis()}"
    }

    private fun openConnection(url: String, timeoutMs: Int = 30_000): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = timeoutMs
            readTimeout = timeoutMs
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

    private fun postJson(connection: HttpURLConnection, request: JSONObject): JSONObject {
        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
            writer.write(request.toString())
        }
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        return if (body.isBlank()) JSONObject() else JSONObject(body)
    }

    private fun localTextFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            "hello" in lower || "hi" in lower || "hey" in lower ->
                "Hey! I'm running in local mode right now. Your chat is working, and you can add a Gemini API key in Settings whenever you want live AI responses."
            "look" in lower || "outfit" in lower ->
                "Your companion is ready for a style check. Open Appearance to change hair, eyes, outfit and background, then generate a portrait."
            "video" in lower ->
                "The Studio is ready. Create a scene, choose camera motion and duration, then play the local preview. Live video generation needs a Gemini/Veo API key."
            else ->
                "Local companion response: I received your message. The app is operating offline, so your conversation remains usable without an API key."
        }
    }

    private fun createLocalImage(prompt: String): Bitmap {
        val bitmap = Bitmap.createBitmap(768, 768, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(0xFF15121F.toInt())

        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF7C4DFF.toInt() }
        canvas.drawCircle(384f, 330f, 190f, glow)
        glow.color = 0xFFFF80AB.toInt()
        canvas.drawCircle(384f, 340f, 135f, glow)

        val head = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFE0BD.toInt() }
        canvas.drawCircle(384f, 335f, 105f, head)

        val hair = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF24202E.toInt() }
        canvas.drawCircle(384f, 285f, 108f, hair)
        canvas.drawRect(276f, 285f, 492f, 345f, hair)

        val eyes = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF00E5FF.toInt() }
        canvas.drawCircle(350f, 335f, 10f, eyes)
        canvas.drawCircle(418f, 335f, 10f, eyes)

        val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("LOCAL PORTRAIT", 384f, 610f, text)
        text.textSize = 18f
        text.typeface = Typeface.DEFAULT
        canvas.drawText(prompt.take(55), 384f, 645f, text)
        return bitmap
    }
}
