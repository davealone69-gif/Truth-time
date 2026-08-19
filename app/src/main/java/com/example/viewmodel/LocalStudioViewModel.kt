package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocalStudioRepository
import com.example.data.local.entity.LocalAvatarEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalStudioViewModel
    @Inject
    constructor(private val repository: LocalStudioRepository) :
    ViewModel() {
        val savedAvatars: StateFlow<List<LocalAvatarEntity>> =
            repository
                .getAllAvatars()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        fun saveNewAvatar(
            modelId: String,
            modelName: String,
            bodyType: String = "",
            wardrobeState: String = "",
            vibeSetting: String = "",
            photoPath: String? = null,
            isUnconstrained: Boolean = true,
        ) {
            viewModelScope.launch {
                val entity =
                    LocalAvatarEntity(
                        modelId = modelId,
                        modelName = modelName,
                        bodyType = bodyType,
                        wardrobeState = wardrobeState,
                        vibeSetting = vibeSetting,
                        localPhotoPath = photoPath,
                        isUnconstrainedActive = isUnconstrained,
                    )
                repository.insertAvatar(entity)
            }
        }
    }
