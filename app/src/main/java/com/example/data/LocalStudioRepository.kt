package com.example.data

import android.content.Context
import com.example.data.local.dao.LocalAvatarDao
import com.example.data.local.entity.LocalAvatarEntity
import com.example.data.models.AdvancedAvatarSpec
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStudioRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val localAvatarDao: LocalAvatarDao,
    ) {
        private val localVaultDir: File by lazy {
            File(context.filesDir, "local_avatar_vault").apply { mkdirs() }
        }

        fun getAllAvatars(): Flow<List<LocalAvatarEntity>> = localAvatarDao.getAllAvatars()

        suspend fun insertAvatar(avatar: LocalAvatarEntity) {
            localAvatarDao.insertAvatar(avatar)
        }

        suspend fun saveAvatarSpecLocally(spec: AdvancedAvatarSpec): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    val file = File(localVaultDir, "${spec.id}_spec.json")
                    val tokenPayload = spec.toAbstractedPromptToken()
                    file.writeText(tokenPayload)
                    true
                } catch (e: Exception) {
                    false
                }
            }

        suspend fun cacheReferenceImage(
            sourcePath: String,
            modelId: String,
        ): String =
            withContext(Dispatchers.IO) {
                val sourceFile = File(sourcePath)
                val destinationFile = File(localVaultDir, "${modelId}_ref.${sourceFile.extension}")
                sourceFile.copyTo(destinationFile, overwrite = true)
                destinationFile.absolutePath
            }
    }
