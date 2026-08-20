package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * OpenAI-compatible LLM client copied from Mandela Matrix OS.
 * No API key is hardcoded. Provider configuration is supplied at runtime.
 */
object LlmClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    data class Config(
        val apiKey: String,
        val baseUrl: String = "https://api.groq.com/openai/v1",
        val model: String = "llama3-8b-8192",
    )

    suspend fun chat(
        config: Config,
        systemPrompt: String? = null,
        userMessage: String,
        history: List<ChatMessage> = emptyList(),
    ): Result<String> = withContext(Dispatchers.IO) {
        if (config.apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("API key required"))
        }

        try {
            val messages = JSONArray()
            if (!systemPrompt.isNullOrBlank()) {
                messages.put(
                    JSONObject()
                        .put("role", "system")
                        .put("content", systemPrompt),
                )
            }
            history.takeLast(12).forEach { message ->
                val role = if (message.role == "assistant") "assistant" else "user"
                messages.put(
                    JSONObject()
                        .put("role", role)
                        .put("content", message.content),
                )
            }
            messages.put(
                JSONObject()
                    .put("role", "user")
                    .put("content", userMessage),
            )

            val body = JSONObject()
                .put("model", config.model)
                .put("messages", messages)
                .put("temperature", 0.7)

            val request = Request.Builder()
                .url("${config.baseUrl.trimEnd('/')}/chat/completions")
                .header("Authorization", "Bearer ${config.apiKey}")
                .header("Content-Type", "application/json")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("HTTP ${response.code}: ${text.take(400)}"),
                    )
                }

                val json = JSONObject(text)
                val content = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                Result.success(content)
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
