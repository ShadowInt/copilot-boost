package ru.copilot.boost.domain.model

data class GraphicsSettings(
    private val values: Map<GraphicsSettingId, Int> = emptyMap(),
) {
    operator fun get(id: GraphicsSettingId): Int =
        values[id]?.coerceIn(0, id.maxLevel) ?: 0

    fun with(id: GraphicsSettingId, value: Int): GraphicsSettings =
        GraphicsSettings(values + (id to value.coerceIn(0, id.maxLevel)))

    fun toKeyValues(): Map<String, String> {
        return GraphicsSettingId.entries.associate { id ->
            id.cfgKey to id.cfgValues[this[id]]
        }
    }

    fun matchingPreset(): GraphicsPresetId? {
        return GraphicsPresetId.entries.firstOrNull { presetId ->
            val presetValues = graphicsPresetValues[presetId] ?: return@firstOrNull false
            GraphicsSettingId.entries.all { settingId ->
                this[settingId] == (presetValues[settingId] ?: 0)
            }
        }
    }

    companion object {
        fun fromPreset(presetId: GraphicsPresetId): GraphicsSettings {
            val presetValues = graphicsPresetValues[presetId] ?: emptyMap()
            return GraphicsSettings(presetValues)
        }

        fun detectFromCfg(content: String): GraphicsSettings {
            val currentValues = parseCfgValues(content)
            val settings = mutableMapOf<GraphicsSettingId, Int>()
            for (id in GraphicsSettingId.entries) {
                val currentValue = currentValues[id.cfgKey.lowercase()]
                if (currentValue != null) {
                    val index = id.cfgValues.indexOf(currentValue)
                    if (index >= 0) {
                        settings[id] = index
                    }
                }
            }
            return GraphicsSettings(settings)
        }

        private fun parseCfgValues(content: String): Map<String, String> {
            val valuesByKey = mutableMapOf<String, String>()
            content.lineSequence().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith(";")) return@forEach
                val key = trimmed
                    .substringBefore("=")
                    .substringBefore(" ")
                    .trim()
                    .lowercase()
                if (key.isEmpty()) return@forEach
                val value = if ('=' in trimmed) {
                    trimmed.substringAfter('=').trim()
                } else {
                    val firstWhitespace = trimmed.indexOfFirst { it.isWhitespace() }
                    if (firstWhitespace < 0) return@forEach
                    trimmed.substring(firstWhitespace).trim()
                }
                if (value.isEmpty()) return@forEach
                valuesByKey[key] = value
            }
            return valuesByKey
        }
    }
}
