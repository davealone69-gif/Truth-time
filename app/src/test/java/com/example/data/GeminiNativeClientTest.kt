package com.example.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GeminiNativeClientTest {
    @Test
    fun `generateText returns error when API key is blank`() =
        runBlocking {
            GeminiNativeClient.API_KEY = ""
            val response = GeminiNativeClient.generateText("Hello")
            assertEquals("Please configure your Gemini API Key in the settings.", response)
        }

    @Test
    fun `generateImage returns null when API key is blank`() =
        runBlocking {
            GeminiNativeClient.API_KEY = ""
            val response = GeminiNativeClient.generateImage("A dog")
            assertEquals(null, response)
        }

    @Test
    fun `generateVideo returns null when API key is blank`() =
        runBlocking {
            GeminiNativeClient.API_KEY = ""
            val response = GeminiNativeClient.generateVideo("A dog walking")
            assertEquals(null, response)
        }
}
