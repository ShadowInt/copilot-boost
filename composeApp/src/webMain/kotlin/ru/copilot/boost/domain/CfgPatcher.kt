package ru.copilot.boost.domain

import ru.copilot.boost.domain.model.AppliedPresetState
import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType

class CfgPatcher {
    data class PreparedCfgContent(
        val lines: List<String>,
        val keys: List<String?>,
    )

    fun prepareContent(content: String): PreparedCfgContent {
        val lines = content.lines()
        val keys = lines.map(::parseCfgKey)
        return PreparedCfgContent(lines = lines, keys = keys)
    }

    fun detectAppliedPresets(content: String): AppliedPresetState {
        val currentValuesByKey = parseCurrentValuesByKey(content)
        return AppliedPresetState(
            disableParasiticParameters = isPresetApplied(currentValuesByKey, parasiticPresetValues),
            disableLegsRendering = isPresetApplied(currentValuesByKey, legsPresetValues),
            disableLegsDeformation = isPresetApplied(currentValuesByKey, legsDeformationPresetValues),
            disableStrobeLights = isPresetApplied(currentValuesByKey, disableStrobeLightsPresetValues),
            reduceHeldItemSize = isPresetApplied(currentValuesByKey, reduceHeldItemSizePresetValues),
            reduceCameraShake = isPresetApplied(currentValuesByKey, reduceCameraShakePresetValues),
            improveTreeMarkerVisibility = isPresetApplied(currentValuesByKey, improveTreeMarkerVisibilityPresetValues),
            disableOcclusionCullingSafeMode = isPresetApplied(currentValuesByKey, disableOcclusionCullingSafeModePresetValues),
            disableGibsCompletely = isPresetApplied(currentValuesByKey, disableGibsCompletelyPresetValues),
        )
    }

    fun applyPresets(
        content: String,
        disableParasiticParameters: Boolean,
        disableLegsRendering: Boolean,
        disableLegsDeformation: Boolean,
        disableStrobeLights: Boolean,
        reduceHeldItemSize: Boolean,
        reduceCameraShake: Boolean,
        improveTreeMarkerVisibility: Boolean,
        disableOcclusionCullingSafeMode: Boolean,
        disableGibsCompletely: Boolean,
        removeParasiticParameters: Boolean = false,
        removeLegsRendering: Boolean = false,
        removeLegsDeformation: Boolean = false,
        removeStrobeLights: Boolean = false,
        removeHeldItemSize: Boolean = false,
        removeCameraShake: Boolean = false,
        removeTreeMarkerVisibility: Boolean = false,
        removeOcclusionCullingSafeMode: Boolean = false,
        removeGibsCompletely: Boolean = false,
        preparedContent: PreparedCfgContent? = null,
    ): CfgPatchResult {
        val activePresetLinesByKey = buildActivePresetLinesByKey(
            disableParasiticParameters = disableParasiticParameters,
            disableLegsRendering = disableLegsRendering,
            disableLegsDeformation = disableLegsDeformation,
            disableStrobeLights = disableStrobeLights,
            reduceHeldItemSize = reduceHeldItemSize,
            reduceCameraShake = reduceCameraShake,
            improveTreeMarkerVisibility = improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = disableOcclusionCullingSafeMode,
            disableGibsCompletely = disableGibsCompletely,
        )
        val keysToRemove = buildKeysToRemove(
            removeParasiticParameters = removeParasiticParameters,
            removeLegsRendering = removeLegsRendering,
            removeLegsDeformation = removeLegsDeformation,
            removeStrobeLights = removeStrobeLights,
            removeHeldItemSize = removeHeldItemSize,
            removeCameraShake = removeCameraShake,
            removeTreeMarkerVisibility = removeTreeMarkerVisibility,
            removeOcclusionCullingSafeMode = removeOcclusionCullingSafeMode,
            removeGibsCompletely = removeGibsCompletely,
        )

        val prepared = preparedContent ?: prepareContent(content)
        val originalLines = prepared.lines
        val originalKeys = prepared.keys
        val updatedLines = mutableListOf<String>()
        val diffRows = mutableListOf<DiffRow>()
        val processedPresetKeys = mutableSetOf<String>()

        originalLines.forEachIndexed { index, line ->
            val key = originalKeys.getOrNull(index)
            if (key == null || !activePresetLinesByKey.containsKey(key)) {
                if (key != null && key in keysToRemove) {
                    diffRows += DiffRow(
                        type = DiffRowType.REMOVED,
                        oldLine = line,
                        newLine = null,
                    )
                    return@forEachIndexed
                }
                updatedLines += line
                diffRows += DiffRow(
                    type = DiffRowType.UNCHANGED,
                    oldLine = line,
                    newLine = line,
                )
                return@forEachIndexed
            }

            if (key in processedPresetKeys) {
                diffRows += DiffRow(
                    type = DiffRowType.REMOVED,
                    oldLine = line,
                    newLine = null,
                )
                return@forEachIndexed
            }

            val canonicalLine = activePresetLinesByKey.getValue(key)
            updatedLines += canonicalLine
            diffRows += if (line.trim() == canonicalLine) {
                DiffRow(
                    type = DiffRowType.UNCHANGED,
                    oldLine = line,
                    newLine = canonicalLine,
                )
            } else {
                DiffRow(
                    type = DiffRowType.MODIFIED,
                    oldLine = line,
                    newLine = canonicalLine,
                )
            }
            processedPresetKeys += key
        }

        val keysToAppend = activePresetLinesByKey.keys - processedPresetKeys
        if (keysToAppend.isNotEmpty()) {
            while (updatedLines.isNotEmpty() && updatedLines.last().isBlank()) {
                updatedLines.removeAt(updatedLines.lastIndex)
                if (diffRows.isNotEmpty()) {
                    diffRows.removeAt(diffRows.lastIndex)
                }
            }
            keysToAppend.forEach { key ->
                val line = activePresetLinesByKey.getValue(key)
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

    companion object {
        private val parasiticPresetValues = linkedMapOf(
            "global.showblood" to "\"False\"",
            "global.censorrecordings" to "\"False\"",
            "shoutcaststreamer.allowinternetstreams" to "\"False\"",
            "effects.hurtoverlay" to "\"False\"",
            "effects.hurtoverleyapplylighting" to "\"False\"",
            "effects.bloom" to "\"False\"",
            "effects.shafts" to "\"False\"",
            "effects.lensdirt" to "\"False\"",
            "graphics.branding" to "\"False\"",
            "gametip.showgametips" to "\"False\"",
            "graphicssettings.particleraycastbudget" to "\"0\"",
            "graphicssettings.pixellightcount" to "\"0\"",
            "ui.showbeltbarbinds" to "\"False\"",
            "water.quality" to "\"0\"",
            "effects.vignet" to "\"False\"",
            "global.processmidiinput" to "\"False\"",
            "player.cold_breath" to "\"False\"",
            "client.hascompletedtutorial" to "\"True\"",
            "render.instanced_rendering" to "\"0\"",
            "graphicssettings.billboardsfacecameraposition" to "\"False\"",
        )

        private val legsPresetValues = linkedMapOf(
            "legs.enablelegs" to "\"False\"",
        )
        private val legsDeformationPresetValues = linkedMapOf(
            "player.footik" to "\"False\"",
        )
        private val disableStrobeLightsPresetValues = linkedMapOf(
            "strobelight.forceoff" to "\"True\"",
        )
        private val reduceHeldItemSizePresetValues = linkedMapOf(
            "graphics.vm_fov_scale" to "\"False\"",
        )
        private val reduceCameraShakePresetValues = linkedMapOf(
            "client.clampscreenshake" to "\"True\"",
            "client.allowcameratiltondpv" to "\"False\"",
            "client.headbob" to "\"False\"",
            "client.hurtpunch" to "\"False\"",
        )
        private val improveTreeMarkerVisibilityPresetValues = linkedMapOf(
            "accessibility.treemarkercolor" to "\"2\"",
        )
        private val disableOcclusionCullingSafeModePresetValues = linkedMapOf(
            "culling.safemode" to "\"False\"",
        )
        private val disableGibsCompletelyPresetValues = linkedMapOf(
            "effects.maxgibdist" to "\"150\"",
            "effects.maxgibs" to "\"0\"",
            "effects.maxgiblife" to "\"0\"",
            "effects.mingiblife" to "\"0\"",
        )

        private val parasiticPresetLinesByKey = parasiticPresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }

        private val legsPresetLinesByKey = legsPresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val legsDeformationPresetLinesByKey = legsDeformationPresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val disableStrobeLightsPresetLinesByKey = disableStrobeLightsPresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val reduceHeldItemSizePresetLinesByKey = reduceHeldItemSizePresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val reduceCameraShakePresetLinesByKey = reduceCameraShakePresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val improveTreeMarkerVisibilityPresetLinesByKey = improveTreeMarkerVisibilityPresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val disableOcclusionCullingSafeModePresetLinesByKey = disableOcclusionCullingSafeModePresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        private val disableGibsCompletelyPresetLinesByKey = disableGibsCompletelyPresetValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }

        private fun buildKeysToRemove(
            removeParasiticParameters: Boolean,
            removeLegsRendering: Boolean,
            removeLegsDeformation: Boolean,
            removeStrobeLights: Boolean,
            removeHeldItemSize: Boolean,
            removeCameraShake: Boolean,
            removeTreeMarkerVisibility: Boolean,
            removeOcclusionCullingSafeMode: Boolean,
            removeGibsCompletely: Boolean,
        ): Set<String> {
            val keysToRemove = linkedSetOf<String>()
            if (removeParasiticParameters) keysToRemove += parasiticPresetLinesByKey.keys
            if (removeLegsRendering) keysToRemove += legsPresetLinesByKey.keys
            if (removeLegsDeformation) keysToRemove += legsDeformationPresetLinesByKey.keys
            if (removeStrobeLights) keysToRemove += disableStrobeLightsPresetLinesByKey.keys
            if (removeHeldItemSize) keysToRemove += reduceHeldItemSizePresetLinesByKey.keys
            if (removeCameraShake) keysToRemove += reduceCameraShakePresetLinesByKey.keys
            if (removeTreeMarkerVisibility) keysToRemove += improveTreeMarkerVisibilityPresetLinesByKey.keys
            if (removeOcclusionCullingSafeMode) keysToRemove += disableOcclusionCullingSafeModePresetLinesByKey.keys
            if (removeGibsCompletely) keysToRemove += disableGibsCompletelyPresetLinesByKey.keys
            return keysToRemove
        }

        private fun buildActivePresetLinesByKey(
            disableParasiticParameters: Boolean,
            disableLegsRendering: Boolean,
            disableLegsDeformation: Boolean,
            disableStrobeLights: Boolean,
            reduceHeldItemSize: Boolean,
            reduceCameraShake: Boolean,
            improveTreeMarkerVisibility: Boolean,
            disableOcclusionCullingSafeMode: Boolean,
            disableGibsCompletely: Boolean,
        ): Map<String, String> {
            val activePresets = linkedMapOf<String, String>()
            if (disableParasiticParameters) {
                activePresets.putAll(parasiticPresetLinesByKey)
            }
            if (disableLegsRendering) {
                activePresets.putAll(legsPresetLinesByKey)
            }
            if (disableLegsDeformation) {
                activePresets.putAll(legsDeformationPresetLinesByKey)
            }
            if (disableStrobeLights) {
                activePresets.putAll(disableStrobeLightsPresetLinesByKey)
            }
            if (reduceHeldItemSize) {
                activePresets.putAll(reduceHeldItemSizePresetLinesByKey)
            }
            if (reduceCameraShake) {
                activePresets.putAll(reduceCameraShakePresetLinesByKey)
            }
            if (improveTreeMarkerVisibility) {
                activePresets.putAll(improveTreeMarkerVisibilityPresetLinesByKey)
            }
            if (disableOcclusionCullingSafeMode) {
                activePresets.putAll(disableOcclusionCullingSafeModePresetLinesByKey)
            }
            if (disableGibsCompletely) {
                activePresets.putAll(disableGibsCompletelyPresetLinesByKey)
            }
            return activePresets
        }

        private fun parseCfgKey(line: String): String? {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith(";")) {
                return null
            }

            val key = trimmed
                .substringBefore("=")
                .substringBefore(" ")
                .trim()
                .lowercase()

            return key.ifEmpty { null }
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
            val key = parseCfgKey(line) ?: return null
            val trimmed = line.trim()
            val value = if ('=' in trimmed) {
                trimmed.substringAfter('=').trim()
            } else {
                val firstWhitespaceIndex = trimmed.indexOfFirst { it.isWhitespace() }
                if (firstWhitespaceIndex < 0) return null
                trimmed.substring(firstWhitespaceIndex).trim()
            }
            if (value.isEmpty()) return null
            return key to value
        }

        private fun isPresetApplied(
            currentValuesByKey: Map<String, String>,
            presetValues: Map<String, String>,
        ): Boolean {
            return presetValues.all { (key, expectedValue) ->
                currentValuesByKey[key.lowercase()] == expectedValue
            }
        }
    }
}
