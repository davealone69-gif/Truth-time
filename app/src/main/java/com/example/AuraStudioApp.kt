package com.example

import android.app.Application
import androidx.work.Configuration
import com.example.workers.LocalSwarmSyncWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AuraStudioApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: LocalSwarmSyncWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
