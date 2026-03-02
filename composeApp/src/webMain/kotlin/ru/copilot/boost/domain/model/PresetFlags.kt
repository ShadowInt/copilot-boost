package ru.copilot.boost.domain.model

data class PresetFlags(
    private val flags: Map<PresetId, Boolean> = emptyMap(),
) {
    operator fun get(id: PresetId): Boolean = flags[id] ?: false

    fun with(id: PresetId, value: Boolean): PresetFlags =
        PresetFlags(flags + (id to value))
}
