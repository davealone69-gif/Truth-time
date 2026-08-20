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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Single native Gemini gateway used by chat, avatar and video features.
 * No API key is ever placed in a URL. Network work stays off the main thread.
 */
object GeminiNativeClient {
    var API_KEY: String = BuildConfig.GEMINI_API_KEY
        set(value) {
            field = value.trim()
        }

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    private const val TEXT_MODEL = "gemini-2.5-flash"
    private const val IMAGE_MODEL = "gemini-2.5-flash-image"
    private const val VIDEO_MODEL = "veo-3.1-generate-preview"

    suspend fun generateText(prompt: String, systemInstruction: String? = null): String =
        withContext(Dispatchers.IO) {
            val key = API_KEY
            if (key.isBlank()) return@withContext localTextFallback(prompt)

            try {
                val connection = openConnection("$BASE_URL/models/$TEXT_MODEL:generateContent")
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
                val response = postJson(connection, request, key)
                if (connection.responseCode in 200..299) {
                    response.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { return@withContext it }
                } else {
                    Log.e("GeminiNativeClient", "Text API ${connection.responseCode}")
                }
            } catch (error: Exception) {
                Log.e("GeminiNativeClient", "Text generation failed", error)
            }
            localTextFallback(prompt)
        }

    suspend fun generateImage(prompt: String): Bitmap = withContext(Dispatchers.IO) {
        val key = API_KEY
        if (key.isBlank()) return@withContext createLocalImage(prompt)

        try {
            val connection = openConnection("$BASE_URL/models/$IMAGE_MODEL:generateContent", 60_000)
            val request = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("IMAGE"))
                    put("imageConfig", JSONObject().put("aspectRatio", "1:1"))
                })
            }
            val response = postJson(connection, request, key)
            if (connection.responseCode in 200..299) {
                val parts = response.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                for (index in 0 until (parts?.length() ?: 0)) {
                    val data = parts?.optJSONObject(index)
                        ?.optJSONObject("inlineData")
                        ?.optString("data")
                    if (!data.isNullOrBlank()) {
                        val bytes = Base64.decode(data, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let {
                            return@withContext it
                        }
                    }
                }
            } else {
                Log.e("GeminiNativeClient", "Image API ${connection.responseCode}")
            }
        } catch (error: Exception) {
            Log.e("GeminiNativeClient", "Image generation failed", error)
        }
        createLocalImage(prompt)
    }

    /**
     * Starts the asynchronous Veo operation, polls it until completion, then downloads
     * the resulting MP4 into the app's private files directory. This is the missing piece
     * that previously returned an operation name as if it were a playable video URL.
     */
    suspend fun generateVideo(prompt: String): String = withContext(Dispatchers.IO) {
        val key = API_KEY
        if (key.isBlank()) return@withContext "local-preview://${System.currentTimeMillis()}"

        try {
            val start = openConnection("$BASE_URL/models/$VIDEO_MODEL:predictLongRunning", 60_000)
            val request = JSONObject().apply {
                put("instances", JSONArray().put(JSONObject().put("prompt", prompt)))
                put("parameters", JSONObject().apply {
                    put("numberOfVideos", 1)
                    put("resolution", "720p")
                    put("aspectRatio", "16:9")
                })
            }
            val startResponse = postJson(start, request, key)
            if (start.responseCode !in 200..299) {
                Log.e("GeminiNativeClient", "Veo start failed: ${start.responseCode}")
                return@withContext "local-preview://${System.currentTimeMillis()}"
            }

            val operationName = startResponse.optString("name").takeIf { it.isNotBlank() }
                ?: return@withContext "local-preview://${System.currentTimeMillis()}"

            var completedResponse: JSONObject? = null
            repeat(36) {
                delay(10_000)
                val poll = openConnection("$BASE_URL/$operationName", 30_000)
                val response = getJson(poll, key)
                if (poll.responseCode !in 200..299) {
                    Log.e("GeminiNativeClient", "Veo poll failed: ${poll.responseCode}")
                    return@repeat
                }
                if (response.optBoolean("done", false)) {
                    completedResponse = response
                    return@repeat
                }
            }

            val finished = completedResponse
                ?: return@withContext "local-preview://${System.currentTimeMillis()}"
            val error = finished.optJSONObject("error")
            if (error != null) {
                Log.e("GeminiNativeClient", "Veo generation failed: $error")
                return@withContext "local-preview://${System.currentTimeMillis()}"
            }

            val videoUri = finished
                .optJSONObject("response")
                ?.optJSONObject("generateVideoResponse")
                ?.optJSONArray("generatedSamples")
                ?.optJSONObject(0)
                ?.optJSONObject("video")
                ?.optString("uri")
                ?.takeIf { it.isNotBlank() }
                ?: return@withContext "local-preview://${System.currentTimeMillis()}"

            val output = File(getApplicationFilesDir(), "video_${System.currentTimeMillis()}.mp4")
            downloadBinary(videoUri, key, output)
            if (output.exists() && output.length() > 0L) {
                return@withContext output.absolutePath
            }
        } catch (error: Exception) {
            Log.e("GeminiNativeClient", "Video generation failed", error)
        }

        "local-preview://${System.currentTimeMillis()}"
    }

    private fun getApplicationFilesDir(): File =
        AppContextHolder.context.filesDir

    private fun openConnection(url: String, timeoutMs: Int = 30_000): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = timeoutMs
            readTimeout = timeoutMs
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

    private fun postJson(connection: HttpURLConnection, request: JSONObject, key: String): JSONObject {
        connection.setRequestProperty("x-goog-api-key", key)
        OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(request.toString()) }
        return readResponse(connection)
    }

    private fun getJson(connection: HttpURLConnection, key: String): JSONObject {
        connection.requestMethod = "GET"
        connection.doOutput = false
        connection.setRequestProperty("x-goog-api-key", key)
        return readResponse(connection)
    }

    private fun readResponse(connection: HttpURLConnection): JSONObject {
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        return if (body.isBlank()) JSONObject() else JSONObject(body)
    }

    private fun downloadBinary(url: String, key: String, output: File) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 30_000
            readTimeout = 120_000
            setRequestProperty("x-goog-api-key", key)
        }
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("Video download failed: ${connection.responseCode}")
        }
        connection.inputStream.use { input -> output.outputStream().use { outputStream -> input.copyTo(outputStream) } }
    }

    private fun localTextFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            "hello" in lower || "hi" in lower || "hey" in lower ->
                "Hey! I'm running in local mode. Chat is available without an API key."
            "look" in lower || "outfit" in lower ->
                "Your companion is ready for a style check. Open Appearance to customise the portrait."
            "video" in lower ->
                "Studio preview is available locally. Live Veo generation needs a Gemini API key."
            else ->
                "Local companion response: I received your message. The app is operating offline."
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
