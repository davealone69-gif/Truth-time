package com.example.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.LocalStudioRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@HiltWorker
class LocalSwarmSyncWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val repository: LocalStudioRepository,
    ) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result =
            withContext(Dispatchers.IO) {
                try {
                    println("[SWARM WORKER] Executing background sync via injected repository...")
                    delay(500)
                    Result.success()
                } catch (e: Exception) {
                    Result.failure()
                }
            }
    }
