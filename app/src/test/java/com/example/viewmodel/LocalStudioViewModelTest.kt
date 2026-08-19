package com.example.viewmodel

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.LocalStudioRepository
import com.example.data.local.LocalAvatarDatabase
import com.example.data.local.dao.LocalAvatarDao
import com.example.ui.viewmodel.LocalStudioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class LocalStudioViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LocalStudioViewModel
    private lateinit var database: LocalAvatarDatabase
    private lateinit var dao: LocalAvatarDao
    private lateinit var repository: LocalStudioRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Application>()
        database =
            Room.inMemoryDatabaseBuilder(context, LocalAvatarDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.localAvatarDao()
        repository = LocalStudioRepository(context, dao)
        viewModel = LocalStudioViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `saveNewAvatar inserts avatar into database`() =
        runTest {
            viewModel.saveNewAvatar(
                modelId = "123",
                modelName = "Test Model",
                bodyType = "Slim",
                wardrobeState = "Casual",
                vibeSetting = "Cyberpunk",
                photoPath = "/path/to/photo.jpg",
                isUnconstrained = true,
            )
            testDispatcher.scheduler.advanceUntilIdle()

            val avatars = repository.getAllAvatars().first()
            assertTrue("Avatars list should not be empty", avatars.isNotEmpty())
            assertEquals("Test Model", avatars.first().modelName)
            assertEquals("123", avatars.first().modelId)
        }
}
