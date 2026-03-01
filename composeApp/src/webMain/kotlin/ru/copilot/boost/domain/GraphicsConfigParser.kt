package ru.copilot.boost.domain

import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.presentation.model.GraphicsPreset
import ru.copilot.boost.presentation.model.GraphicsUiState

/**
 * Парсит и применяет настройки графики к конфигу.
 * Ключи соответствуют Rust client.cfg / config.cfg.
 */
object GraphicsConfigParser {
    private const val SHADOW_CASCADES = "graphics.shadowcascades"
    private const val SHADOW_DISTANCE = "graphics.shadowdistance"
    private const val SHADOW_LIGHTS = "graphics.shadowlights"
    private const val SHADER_LOD = "graphics.shaderlod"
    private const val TREE_QUALITY = "tree.quality"
    private const val WATER_REFLECTIONS = "water.reflections"
    private const val GRASS_QUALITY = "grass.quality"
    private const val PARTICLE_QUALITY = "particle.quality"
    private const val EFFECTS_AA = "effects.aa"

    fun parseFromContent(content: String): GraphicsUiState {
        val valuesByKey = parseCurrentValuesByKey(content)
        return GraphicsUiState(
            shadowQuality = parseShadowQuality(valuesByKey),
            textureQuality = parseShaderLod(valuesByKey),
            lightingQuality = parseShadowLights(valuesByKey),
            treeQuality = parseTreeQuality(valuesByKey),
            waterReflections = parseWaterReflections(valuesByKey),
            grassQuality = parseGrassQuality(valuesByKey),
            cloudQuality = parseParticleQuality(valuesByKey),
            antialiasing = parseEffectsAa(valuesByKey),
        )
    }

    fun applyToContent(content: String, state: GraphicsUiState): CfgPatchResult {
        val valuesByKey = parseCurrentValuesByKey(content).toMutableMap()
        valuesByKey[SHADOW_CASCADES] = shadowCascadesFromSlider(state.shadowQuality).toString()
        valuesByKey[SHADOW_DISTANCE] = shadowDistanceFromSlider(state.shadowQuality).toString()
        valuesByKey[SHADOW_LIGHTS] = shadowLightsFromSlider(state.lightingQuality).toString()
        valuesByKey[SHADER_LOD] = shaderLodFromSlider(state.textureQuality).toString()
        valuesByKey[TREE_QUALITY] = treeQualityFromSlider(state.treeQuality).toString()
        valuesByKey[WATER_REFLECTIONS] = waterReflectionsFromSlider(state.waterReflections).toString()
        valuesByKey[GRASS_QUALITY] = grassQualityFromSlider(state.grassQuality).toString()
        valuesByKey[PARTICLE_QUALITY] = particleQualityFromSlider(state.cloudQuality).toString()
        valuesByKey[EFFECTS_AA] = effectsAaFromSlider(state.antialiasing).toString()
        return applyValuesToContent(content, valuesByKey)
    }

    private fun parseCurrentValuesByKey(content: String): Map<String, String> {
        val valuesByKey = mutableMapOf<String, String>()
        content.lineSequence().forEach { line ->
            val parsed = parseCfgEntry(line) ?: return@forEach
            valuesByKey[parsed.first] = parsed.second
        }
        return valuesByKey
    }

    private fun parseCfgEntry(line: String): Pair<String, String>? {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith(";")) return null
        val key = trimmed.substringBefore("=").substringBefore(" ").trim().lowercase()
        if (key.isEmpty()) return null
        val value = if ('=' in trimmed) {
            trimmed.substringAfter('=').trim().trim('"')
        } else {
            val firstWhitespaceIndex = trimmed.indexOfFirst { it.isWhitespace() }
            if (firstWhitespaceIndex < 0) return null
            trimmed.substring(firstWhitespaceIndex).trim().trim('"')
        }
        return if (value.isEmpty()) null else key to value
    }

    private fun parseShadowQuality(values: Map<String, String>): Int {
        val cascades = values[SHADOW_CASCADES]?.toIntOrNull() ?: 1
        val distance = values[SHADOW_DISTANCE]?.toIntOrNull() ?: 100
        val cascadePart = ((cascades - 1) / 3f * 50).toInt().coerceIn(0, 50)
        val distancePart = ((distance - 50).coerceIn(0, 950) / 950f * 50).toInt().coerceIn(0, 50)
        return (cascadePart + distancePart).coerceIn(0, 100)
    }

    private fun shadowCascadesFromSlider(v: Int): Int = when {
        v < 25 -> 1
        v < 50 -> 2
        else -> 4
    }

    private fun shadowDistanceFromSlider(v: Int): Int = 50 + (v / 100f * 950).toInt().coerceIn(0, 950)

    private fun parseShaderLod(values: Map<String, String>): Int {
        val lod = values[SHADER_LOD]?.toIntOrNull() ?: 600
        return ((lod - 100) / 500f * 100).toInt().coerceIn(0, 100)
    }

    private fun shaderLodFromSlider(v: Int): Int = 100 + (v / 100f * 500).toInt()

    private fun parseShadowLights(values: Map<String, String>): Int {
        val lights = values[SHADOW_LIGHTS]?.toIntOrNull() ?: 1
        return (lights / 3f * 100).toInt().coerceIn(0, 100)
    }

    private fun shadowLightsFromSlider(v: Int): Int = (v / 100f * 3).toInt().coerceIn(0, 3)

    private fun parseTreeQuality(values: Map<String, String>): Int {
        val q = values[TREE_QUALITY]?.toIntOrNull() ?: 100
        return (q / 200f * 100).toInt().coerceIn(0, 100)
    }

    private fun treeQualityFromSlider(v: Int): Int = (v / 100f * 200).toInt().coerceIn(0, 200)

    private fun parseWaterReflections(values: Map<String, String>): Int {
        val r = values[WATER_REFLECTIONS]?.toIntOrNull() ?: 1
        return (r / 4f * 100).toInt().coerceIn(0, 100)
    }

    private fun waterReflectionsFromSlider(v: Int): Int = (v / 100f * 4).toInt().coerceIn(0, 4)

    private fun parseGrassQuality(values: Map<String, String>): Int {
        return values[GRASS_QUALITY]?.toIntOrNull() ?: 100
    }

    private fun grassQualityFromSlider(v: Int): Int = v.coerceIn(0, 100)

    private fun parseParticleQuality(values: Map<String, String>): Int {
        return values[PARTICLE_QUALITY]?.toIntOrNull() ?: 100
    }

    private fun particleQualityFromSlider(v: Int): Int = v.coerceIn(0, 100)

    private fun parseEffectsAa(values: Map<String, String>): Int {
        val aa = values[EFFECTS_AA]?.toIntOrNull() ?: 1
        return (aa / 3f * 100).toInt().coerceIn(0, 100)
    }

    private fun effectsAaFromSlider(v: Int): Int = (v / 100f * 3).toInt().coerceIn(0, 3)

    private fun applyValuesToContent(content: String, valuesByKey: Map<String, String>): CfgPatchResult {
        val originalLines = content.lines().toMutableList()
        val updatedLines = mutableListOf<String>()
        val diffRows = mutableListOf<DiffRow>()
        val keysToUpdate = valuesByKey.keys
        val updatedKeys = mutableSetOf<String>()

        originalLines.forEach { line ->
            val parsed = parseCfgEntry(line)
            if (parsed != null && parsed.first in keysToUpdate) {
                val key = parsed.first
                val newValue = valuesByKey.getValue(key)
                val newLine = "$key \"$newValue\""
                updatedLines += newLine
                diffRows += if (line.trim() == newLine.trim()) {
                    DiffRow(
                        type = DiffRowType.UNCHANGED,
                        oldLine = line,
                        newLine = newLine,
                    )
                } else {
                    DiffRow(
                        type = DiffRowType.MODIFIED,
                        oldLine = line,
                        newLine = newLine,
                    )
                }
                updatedKeys += key
            } else {
                updatedLines += line
                diffRows += DiffRow(
                    type = DiffRowType.UNCHANGED,
                    oldLine = line,
                    newLine = line,
                )
            }
        }

        val existingKeys = originalLines.mapNotNull { parseCfgEntry(it)?.first }.toSet()
        val missingKeys = keysToUpdate - existingKeys
        if (missingKeys.isNotEmpty()) {
            // убираем хвостовые пустые строки перед добавлением
            while (updatedLines.isNotEmpty() && updatedLines.last().isBlank()) {
                updatedLines.removeAt(updatedLines.lastIndex)
                if (diffRows.isNotEmpty()) {
                    diffRows.removeAt(diffRows.lastIndex)
                }
            }
            missingKeys.forEach { key ->
                val value = valuesByKey.getValue(key)
                val line = "$key \"$value\""
                updatedLines += line
                diffRows += DiffRow(
                    type = DiffRowType.ADDED,
                    oldLine = null,
                    newLine = line,
                )
            }
        }

        return CfgPatchResult(
            updatedContent = updatedLines.joinToString("\n"),
            diffRows = diffRows,
        )
    }

    fun presetToState(preset: GraphicsPreset): GraphicsUiState {
        return when (preset) {
            GraphicsPreset.PERFORMANCE -> GraphicsUiState(
                shadowQuality = 0,
                textureQuality = 0,
                lightingQuality = 0,
                treeQuality = 0,
                waterReflections = 0,
                grassQuality = 0,
                cloudQuality = 0,
                antialiasing = 0,
            )
            GraphicsPreset.COMBAT -> GraphicsUiState(
                shadowQuality = 25,
                textureQuality = 40,
                lightingQuality = 33,
                treeQuality = 30,
                waterReflections = 0,
                grassQuality = 20,
                cloudQuality = 30,
                antialiasing = 33,
            )
            GraphicsPreset.BALANCE -> GraphicsUiState(
                shadowQuality = 50,
                textureQuality = 50,
                lightingQuality = 50,
                treeQuality = 50,
                waterReflections = 50,
                grassQuality = 50,
                cloudQuality = 50,
                antialiasing = 50,
            )
            GraphicsPreset.GRAPHICS -> GraphicsUiState(
                shadowQuality = 100,
                textureQuality = 100,
                lightingQuality = 100,
                treeQuality = 100,
                waterReflections = 100,
                grassQuality = 100,
                cloudQuality = 100,
                antialiasing = 100,
            )
            GraphicsPreset.CUSTOM -> throw IllegalArgumentException("Custom preset has no default values")
        }
    }
}
