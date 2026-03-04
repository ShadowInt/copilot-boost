package ru.copilot.boost.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ApplyInstructionsStore : SetupModuleStore {
    var isTweaksDownloadTriggered by mutableStateOf(false)
        private set

    private var _maxReachedStageIndices by mutableStateOf<Map<SetupModuleId, Int>>(emptyMap())

    fun onTweaksDownloaded() {
        isTweaksDownloadTriggered = true
    }

    fun maxReachedStageIndex(moduleId: SetupModuleId, isActivated: Boolean): Int {
        val minimum = if (isActivated) 1 else 0
        return (_maxReachedStageIndices[moduleId] ?: 0).coerceAtLeast(minimum)
    }

    fun updateMaxReachedStageIndex(moduleId: SetupModuleId, index: Int) {
        val current = _maxReachedStageIndices[moduleId] ?: 0
        if (index > current) {
            _maxReachedStageIndices = _maxReachedStageIndices + (moduleId to index)
        }
    }

    override fun reset() {
        isTweaksDownloadTriggered = false
        _maxReachedStageIndices = emptyMap()
    }
}
