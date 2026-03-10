package ru.copilot.boost.domain.model

enum class GraphicsPresetId {
    Performance,
    Combat,
    Balance,
    Graphics,
}

val graphicsPresetValues: Map<GraphicsPresetId, Map<GraphicsSettingId, Int>> = mapOf(
    GraphicsPresetId.Performance to mapOf(
        GraphicsSettingId.ShadowQuality to 0,
        GraphicsSettingId.TextureQuality to 0,
        GraphicsSettingId.LightingQuality to 0,
        GraphicsSettingId.TreeQuality to 0,
        GraphicsSettingId.WaterReflections to 0,
        GraphicsSettingId.GrassQuality to 0,
        GraphicsSettingId.CloudQuality to 0,
        GraphicsSettingId.AntiAliasing to 0,
    ),
    GraphicsPresetId.Combat to mapOf(
        GraphicsSettingId.ShadowQuality to 0,
        GraphicsSettingId.TextureQuality to 2,
        GraphicsSettingId.LightingQuality to 1,
        GraphicsSettingId.TreeQuality to 1,
        GraphicsSettingId.WaterReflections to 0,
        GraphicsSettingId.GrassQuality to 1,
        GraphicsSettingId.CloudQuality to 0,
        GraphicsSettingId.AntiAliasing to 1,
    ),
    GraphicsPresetId.Balance to mapOf(
        GraphicsSettingId.ShadowQuality to 2,
        GraphicsSettingId.TextureQuality to 2,
        GraphicsSettingId.LightingQuality to 2,
        GraphicsSettingId.TreeQuality to 2,
        GraphicsSettingId.WaterReflections to 2,
        GraphicsSettingId.GrassQuality to 2,
        GraphicsSettingId.CloudQuality to 2,
        GraphicsSettingId.AntiAliasing to 2,
    ),
    GraphicsPresetId.Graphics to mapOf(
        GraphicsSettingId.ShadowQuality to 4,
        GraphicsSettingId.TextureQuality to 4,
        GraphicsSettingId.LightingQuality to 4,
        GraphicsSettingId.TreeQuality to 4,
        GraphicsSettingId.WaterReflections to 4,
        GraphicsSettingId.GrassQuality to 4,
        GraphicsSettingId.CloudQuality to 4,
        GraphicsSettingId.AntiAliasing to 4,
    ),
)
