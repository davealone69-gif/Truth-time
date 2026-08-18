package com.example.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.data.repository.LocalStudioRepository
import javax.inject.Inject

class LocalSwarmSyncWorkerFactory
@Inject
constructor(private val repository: LocalStudioRepository) : WorkerFactory() {

  override fun createWorker(
      appContext: Context,
      workerClassName: String,
      workerParameters: WorkerParameters
  ): ListenableWorker? {
    return when (workerClassName) {
      LocalSwarmSyncWorker::class.java.name -> {
        LocalSwarmSyncWorker(appContext, workerParameters, repository)
      }
      else -> null
    }
  }
}
