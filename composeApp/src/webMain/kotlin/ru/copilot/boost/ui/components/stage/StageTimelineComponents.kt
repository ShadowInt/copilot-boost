package ru.copilot.boost.ui.components.stage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun StageTimeline(
    stages: List<StageDefinition>,
    stageStatusProvider: (Int) -> StageStatus,
    stageModifiers: Map<Int, Modifier> = emptyMap(),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        stages.forEachIndexed { index, stage ->
            key(index) {
                StageTimelineRow(
                    modifier = stageModifiers[index] ?: Modifier,
                    definition = stage,
                    status = stageStatusProvider(index),
                    showConnector = index != stages.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun StageTimelineRow(
    modifier: Modifier = Modifier,
    definition: StageDefinition,
    status: StageStatus,
    showConnector: Boolean,
) {
    val markerColor = stageStatusColor(status)
    val labelColor = stageStatusTextColor(status)
    val isStarted = status != StageStatus.NOT_STARTED
    var contentHeightPx by remember { mutableIntStateOf(0) }
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        StageMarkerColumn(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight(),
            color = markerColor,
            showConnector = showConnector,
            contentHeightPx = contentHeightPx,
            markerTopOffset = 5.dp,
        )
        Column(
            modifier = Modifier
                .padding(start = 24.dp)
                .onSizeChanged { contentHeightPx = it.height },
        ) {
            Text(
                text = definition.text,
                style = MaterialTheme.typography.bodyMedium,
                color = labelColor,
                modifier = Modifier.padding(top = 0.dp, end = 4.dp),
            )
            if (isStarted) {
                when {
                    definition.content != null -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        definition.content.invoke(status)
                    }
                    definition.imageResource != null -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(definition.imageResource),
                            contentDescription = definition.text,
                            modifier = Modifier
                                .fillMaxWidth(0.82f),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StageMarkerColumn(
    modifier: Modifier = Modifier,
    color: Color,
    showConnector: Boolean,
    contentHeightPx: Int,
    markerTopOffset: Dp = 0.dp,
) {
    val density = LocalDensity.current
    val markerColumnHeight = with(density) {
        if (contentHeightPx > 0) contentHeightPx.toDp() else 10.dp
    }
    Box(
        modifier = modifier
            .width(16.dp)
            .height(markerColumnHeight)
            .drawBehind {
                if (showConnector) {
                    val markerSize = 10.dp.toPx()
                    val gapBelowMarker = 2.dp.toPx()
                    val lineWidth = 2.dp.toPx()
                    val startY = markerTopOffset.toPx() + markerSize + gapBelowMarker
                    val lineHeight = (size.height - startY).coerceAtLeast(0f)
                    drawRect(
                        color = color.copy(alpha = 0.55f),
                        topLeft = Offset((size.width - lineWidth) / 2f, startY),
                        size = Size(lineWidth, lineHeight),
                    )
                }
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(top = markerTopOffset)
                .size(10.dp)
                .background(
                    color = color,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                ),
        )
    }
}

enum class StageStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}

data class StageDefinition(
    val text: String,
    val imageResource: DrawableResource? = null,
    val content: (@Composable (StageStatus) -> Unit)? = null,
)

@Composable
fun stageStatusColor(status: StageStatus): Color = when (status) {
    StageStatus.NOT_STARTED -> MaterialTheme.colorScheme.outlineVariant
    StageStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    StageStatus.COMPLETED -> Color(0xFF2E7D32)
}

@Composable
fun stageStatusTextColor(status: StageStatus): Color = when (status) {
    StageStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
    StageStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onSurface
    StageStatus.COMPLETED -> MaterialTheme.colorScheme.onSurface
}
