package com.example.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null,
)

@Serializable
data class Content(
    val parts: List<Part>,
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null,
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String,
)

@Serializable
data class GenerationConfig(
    val responseModalities: List<String>? = null,
    val imageConfig: ImageConfig? = null,
)

@Serializable
data class ImageConfig(
    val aspectRatio: String? = null,
    val outputMimeType: String? = null,
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null,
    val error: JsonObject? = null,
)

@Serializable
data class Candidate(
    val content: Content? = null,
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest,
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()

    val service: GeminiApiService by lazy {
        val json =
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            }
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

suspend fun generateAvatarImage(prompt: String): Bitmap? =
    withContext<Bitmap?>(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) return@withContext null

        val request =
            GenerateContentRequest(
                contents =
                    listOf(
                        Content(
                            parts = listOf(Part(text = prompt)),
                        ),
                    ),
                generationConfig =
                    GenerationConfig(
                        responseModalities = listOf("IMAGE"),
                        imageConfig = ImageConfig(aspectRatio = "1:1", outputMimeType = "image/jpeg"),
                    ),
            )

        try {
            val response = RetrofitClient.service.generateContent("gemini-2.5-flash-image", apiKey, request)
            val base64Image = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data
            if (base64Image != null) {
                val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                return@withContext BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
