package ru.copilot.boost.domain

import ru.copilot.boost.domain.model.AppliedPresetState
import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType

class CfgPatcher {
    fun detectAppliedPresets(content: String): AppliedPresetState {
        val currentValuesByKey = parseCurrentValuesByKey(content)
        return AppliedPresetState(
            disableParasiticParameters = isPresetApplied(currentValuesByKey, parasiticPresetValues),
            disableLegsRendering = isPresetApplied(currentValuesByKey, legsPresetValues),
            disableLegsDeformation = isPresetApplied(currentValuesByKey, legsDeformationPresetValues),
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
        reduceCameraShake: Boolean,
        improveTreeMarkerVisibility: Boolean,
        disableOcclusionCullingSafeMode: Boolean,
        disableGibsCompletely: Boolean,
    ): CfgPatchResult {
        val activePresetLinesByKey = buildActivePresetLinesByKey(
            disableParasiticParameters = disableParasiticParameters,
            disableLegsRendering = disableLegsRendering,
            disableLegsDeformation = disableLegsDeformation,
            reduceCameraShake = reduceCameraShake,
            improveTreeMarkerVisibility = improveTreeMarkerVisibility,
            disableOcclusionCullingSafeMode = disableOcclusionCullingSafeMode,
            disableGibsCompletely = disableGibsCompletely,
        )

        if (activePresetLinesByKey.isEmpty()) {
            val originalLines = content.lines()
            return CfgPatchResult(
                updatedContent = content,
                diffRows = originalLines.map { line ->
                    DiffRow(
                        type = DiffRowType.UNCHANGED,
                        oldLine = line,
                        newLine = line,
                    )
                },
            )
        }

        val originalLines = content.lines()
        val updatedLines = mutableListOf<String>()
        val diffRows = mutableListOf<DiffRow>()
        val processedPresetKeys = mutableSetOf<String>()

        originalLines.forEach { line ->
            val key = parseCfgKey(line)
            if (key == null || key !in activePresetLinesByKey.keys) {
                updatedLines += line
                diffRows += DiffRow(
                    type = DiffRowType.UNCHANGED,
                    oldLine = line,
                    newLine = line,
                )
                return@forEach
            }

            if (key in processedPresetKeys) {
                diffRows += DiffRow(
                    type = DiffRowType.REMOVED,
                    oldLine = line,
                    newLine = null,
                )
                return@forEach
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
            if (updatedLines.isNotEmpty() && updatedLines.last().isNotBlank()) {
                updatedLines += ""
                diffRows += DiffRow(
                    type = DiffRowType.ADDED,
                    oldLine = null,
                    newLine = "",
                )
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

        private fun buildActivePresetLinesByKey(
            disableParasiticParameters: Boolean,
            disableLegsRendering: Boolean,
            disableLegsDeformation: Boolean,
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
                val parts = trimmed.split(Regex("\\s+"), limit = 2)
                if (parts.size < 2) return null
                parts[1].trim()
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
