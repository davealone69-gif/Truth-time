package com.example.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuraViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuraViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = AuraViewModel(application)
        testDispatcher.scheduler.advanceUntilIdle() // let init blocks run
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test sending message updates state`() =
        runTest {
            viewModel.sendMessage("Hello there")

            val messages = viewModel.messages.value
            assertTrue("Messages should not be empty", messages.isNotEmpty())
            assertEquals("User message should be added", "Hello there", messages.last().text)
        }
}
