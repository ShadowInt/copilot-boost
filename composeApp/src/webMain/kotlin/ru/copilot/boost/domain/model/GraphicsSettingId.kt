package ru.copilot.boost.domain.model

enum class GraphicsSettingId(
    val cfgKey: String,
    val cfgValues: List<String>,
) {
    ShadowQuality(
        cfgKey = "graphics.shadowquality",
        cfgValues = listOf("\"0\"", "\"1\"", "\"2\"", "\"3\"", "\"4\""),
    ),
    TextureQuality(
        cfgKey = "graphics.texturequality",
        cfgValues = listOf("\"0\"", "\"1\"", "\"2\"", "\"3\"", "\"4\""),
    ),
    LightingQuality(
        cfgKey = "graphics.shadowlights",
        cfgValues = listOf("\"0\"", "\"1\"", "\"2\"", "\"3\"", "\"4\""),
    ),
    TreeQuality(
        cfgKey = "tree.quality",
        cfgValues = listOf("\"0\"", "\"50\"", "\"100\"", "\"150\"", "\"200\""),
    ),
    WaterReflections(
        cfgKey = "water.reflections",
        cfgValues = listOf("\"0\"", "\"1\"", "\"2\"", "\"3\"", "\"4\""),
    ),
    GrassQuality(
        cfgKey = "grass.quality",
        cfgValues = listOf("\"0\"", "\"25\"", "\"50\"", "\"75\"", "\"100\""),
    ),
    CloudQuality(
        cfgKey = "cloud.quality",
        cfgValues = listOf("\"0\"", "\"1\"", "\"2\"", "\"3\"", "\"4\""),
    ),
    AntiAliasing(
        cfgKey = "graphics.antialiasing",
        cfgValues = listOf("\"0\"", "\"1\"", "\"2\"", "\"3\"", "\"4\""),
    ),
    ;

    val maxLevel: Int get() = cfgValues.lastIndex
}
