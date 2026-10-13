package com.example.data

import android.graphics.Bitmap
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GeminiNativeClientTest {
    private val originalApiKey = GeminiNativeClient.API_KEY

    @After
    fun restoreApiKey() {
        GeminiNativeClient.API_KEY = originalApiKey
    }

    @Test
    fun `generateText uses the documented local fallback when API key is blank`() = runBlocking {
        GeminiNativeClient.API_KEY = ""
        val response = GeminiNativeClient.generateText("Hello")
        assertEquals(
            "Hey! I'm running in local mode. Chat is available without an API key.",
            response
        )
    }

    @Test
    fun `generateImage returns a local preview bitmap when API key is blank`() = runBlocking {
        GeminiNativeClient.API_KEY = ""
        val response: Bitmap = GeminiNativeClient.generateImage("A dog")
        assertNotNull(response)
        assertEquals(768, response.width)
        assertEquals(768, response.height)
        response.recycle()
    }

    @Test
    fun `generateVideo returns a clearly marked local preview when API key is blank`() = runBlocking {
        GeminiNativeClient.API_KEY = ""
        val response = GeminiNativeClient.generateVideo("A dog walking")
        assertTrue("Expected local-preview URI but got: $response", response.startsWith("local-preview://"))
    }
}
