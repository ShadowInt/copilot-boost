package ru.copilot.boost.domain

import ru.copilot.boost.domain.model.CfgPatchResult
import ru.copilot.boost.domain.model.DiffRow
import ru.copilot.boost.domain.model.DiffRowType
import ru.copilot.boost.domain.model.PresetFlags
import ru.copilot.boost.domain.model.PresetId
import ru.copilot.boost.domain.model.PreparedCfgContent

class CfgPatcher {
    fun prepareContent(content: String): PreparedCfgContent {
        val lines = content.lines()
        val keys = lines.map(::parseCfgKey)
        return PreparedCfgContent(lines = lines, keys = keys)
    }

    fun detectAppliedPresets(content: String): PresetFlags {
        val currentValuesByKey = parseCurrentValuesByKey(content)
        val flags = PresetId.entries.associateWith { id ->
            isPresetApplied(currentValuesByKey, presetValuesById.getValue(id))
        }
        return PresetFlags(flags)
    }

    fun applyPresets(
        content: String,
        enabled: PresetFlags,
        remove: PresetFlags = PresetFlags(),
        preparedContent: PreparedCfgContent? = null,
    ): CfgPatchResult {
        val activePresetLinesByKey = buildActivePresetLinesByKey(enabled)
        val keysToRemove = buildKeysToRemove(remove)
        return applyChanges(content, activePresetLinesByKey, keysToRemove, preparedContent)
    }

    fun applyKeyValues(
        content: String,
        keyValues: Map<String, String>,
        preparedContent: PreparedCfgContent? = null,
    ): CfgPatchResult {
        val linesByKey = keyValues
            .mapKeys { it.key.lowercase() }
            .mapValues { (key, value) -> "$key $value" }
        return applyChanges(content, linesByKey, emptySet(), preparedContent)
    }

    private fun applyChanges(
        content: String,
        activeLinesByKey: Map<String, String>,
        keysToRemove: Set<String>,
        preparedContent: PreparedCfgContent?,
    ): CfgPatchResult {
        val prepared = preparedContent ?: prepareContent(content)
        val originalLines = prepared.lines
        val originalKeys = prepared.keys
        val updatedLines = mutableListOf<String>()
        val diffRows = mutableListOf<DiffRow>()
        val processedPresetKeys = mutableSetOf<String>()

        originalLines.forEachIndexed { index, line ->
            val key = originalKeys.getOrNull(index)
            if (key == null || !activeLinesByKey.containsKey(key)) {
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

            val canonicalLine = activeLinesByKey.getValue(key)
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

        val keysToAppend = activeLinesByKey.keys - processedPresetKeys
        if (keysToAppend.isNotEmpty()) {
            while (updatedLines.isNotEmpty() && updatedLines.last().isBlank()) {
                updatedLines.removeAt(updatedLines.lastIndex)
                if (diffRows.isNotEmpty()) {
                    diffRows.removeAt(diffRows.lastIndex)
                }
            }
            keysToAppend.forEach { key ->
                val line = activeLinesByKey.getValue(key)
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
        private val presetValuesById: Map<PresetId, LinkedHashMap<String, String>> = mapOf(
            PresetId.DisableParasiticParameters to linkedMapOf(
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
            ),
            PresetId.DisableLegsRendering to linkedMapOf(
                "legs.enablelegs" to "\"False\"",
            ),
            PresetId.DisableLegsDeformation to linkedMapOf(
                "player.footik" to "\"False\"",
            ),
            PresetId.DisableStrobeLights to linkedMapOf(
                "strobelight.forceoff" to "\"True\"",
            ),
            PresetId.ReduceHeldItemSize to linkedMapOf(
                "graphics.vm_fov_scale" to "\"False\"",
            ),
            PresetId.RestoreEventTextNotifications to linkedMapOf(
                "ui.monumentnotificationtoasts" to "\"True\"",
            ),
            PresetId.RemoveAutocraftMenuDelay to linkedMapOf(
                "inventory.quickcraftdelay" to "\"0\"",
            ),
            PresetId.ReduceSleepingBagRemovalDelay to linkedMapOf(
                "client.bag_unclaim_duration" to "\"0.1\"",
            ),
            PresetId.AddMapInfoToF8Menu to linkedMapOf(
                "debug.showworldinfoinperformancereadout" to "\"True\"",
            ),
            PresetId.DisableClientErrorOverlay to linkedMapOf(
                "console.erroroverlay" to "\"False\"",
            ),
            PresetId.AddAdminGesturesToGameMenu to linkedMapOf(
                "gesturecollection.showadmincinematicgesturesinbindings" to "\"True\"",
            ),
            PresetId.ConvenientSkinSorting to linkedMapOf(
                "client.sortskinsrecentlyused" to "\"True\"",
            ),
            PresetId.EnlargedConsole to linkedMapOf(
                "global.consolescale" to "\"16\"",
            ),
            PresetId.ReduceRadialMenuCallDelay to linkedMapOf(
                "input.holdtime" to "\"0.15\"",
            ),
            PresetId.LeftHandMode to linkedMapOf(
                "graphics.vm_horizontal_flip" to "\"True\"",
            ),
            PresetId.ReduceCameraShake to linkedMapOf(
                "client.clampscreenshake" to "\"True\"",
                "client.allowcameratiltondpv" to "\"False\"",
                "client.headbob" to "\"False\"",
                "client.hurtpunch" to "\"False\"",
            ),
            PresetId.ImproveTreeMarkerVisibility to linkedMapOf(
                "accessibility.treemarkercolor" to "\"2\"",
            ),
            PresetId.DisableOcclusionCullingSafeMode to linkedMapOf(
                "culling.safemode" to "\"False\"",
            ),
            PresetId.DisableGibsCompletely to linkedMapOf(
                "effects.maxgibdist" to "\"150\"",
                "effects.maxgibs" to "\"0\"",
                "effects.maxgiblife" to "\"0\"",
                "effects.mingiblife" to "\"0\"",
            ),
        )

        private val presetLinesByKeyById: Map<PresetId, Map<String, String>> =
            presetValuesById.mapValues { (_, values) -> canonicalPresetLinesByKey(values) }

        private fun buildActivePresetLinesByKey(enabled: PresetFlags): Map<String, String> {
            val activePresets = linkedMapOf<String, String>()
            PresetId.entries.forEach { id ->
                if (enabled[id]) {
                    activePresets.putAll(presetLinesByKeyById.getValue(id))
                }
            }
            return activePresets
        }

        private fun buildKeysToRemove(remove: PresetFlags): Set<String> {
            val keysToRemove = linkedSetOf<String>()
            PresetId.entries.forEach { id ->
                if (remove[id]) {
                    keysToRemove += presetLinesByKeyById.getValue(id).keys
                }
            }
            return keysToRemove
        }

        private fun canonicalPresetLinesByKey(values: Map<String, String>): Map<String, String> {
            return values.mapKeys { it.key.lowercase() }
                .mapValues { (key, value) -> "$key $value" }
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
