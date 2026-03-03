package ru.copilot.boost.ui.components.stage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import copilotboost.composeapp.generated.resources.Res
import copilotboost.composeapp.generated.resources.apply_skipped
import copilotboost.composeapp.generated.resources.apply_status_completed
import copilotboost.composeapp.generated.resources.apply_status_in_progress
import org.jetbrains.compose.resources.stringResource
import ru.copilot.boost.ui.components.ModuleInstructionCard

@Composable
fun StageInstructionCard(
    modifier: Modifier = Modifier,
    title: String,
    stages: List<StageDefinition>,
    isActivated: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit,
    hasContent: Boolean = true,
) {
    val totalStages = stages.size
    var maxReachedStageIndex by remember(isActivated, totalStages) {
        mutableIntStateOf(if (isActivated) 1 else 0)
    }
    ModuleInstructionCard(
        modifier = modifier,
        title = title,
        expanded = expanded,
        onToggle = onToggle,
        statusIcon = if (isActivated && maxReachedStageIndex >= (totalStages - 1)) {
            Icons.Filled.CheckCircle
        } else {
            Icons.Filled.Schedule
        },
        statusIconTint = if (isActivated && maxReachedStageIndex >= (totalStages - 1)) {
            Color(0xFF2E7D32)
        } else {
            Color(0xFFF9A825)
        },
        statusContentDescription = if (isActivated && maxReachedStageIndex >= (totalStages - 1)) {
            stringResource(Res.string.apply_status_completed)
        } else {
            stringResource(Res.string.apply_status_in_progress)
        },
    ) {
        if (!hasContent) {
            SkippedLabel()
            return@ModuleInstructionCard
        }

        val scrollState = rememberScrollState()

        val scrollValue = scrollState.value
        val maxScrollValue = scrollState.maxValue
        val activeStageIndex = if (maxScrollValue > 0) {
            ((scrollValue.toFloat() / maxScrollValue) * totalStages)
                .toInt()
                .coerceIn(0, totalStages - 1)
        } else {
            0
        }
        val isAtBottom = maxScrollValue > 0 && scrollValue >= (maxScrollValue - 24)
        val currentReachedStage = when {
            !isActivated -> 0
            isAtBottom -> totalStages - 1
            else -> activeStageIndex.coerceAtLeast(1)
        }
        if (currentReachedStage > maxReachedStageIndex) {
            maxReachedStageIndex = currentReachedStage
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
        ) {
            key(maxReachedStageIndex, isActivated) {
                StageTimeline(
                    stages = stages,
                    stageStatusProvider = { index ->
                        stageStatusFromProgress(
                            index = index,
                            isActivated = isActivated,
                            maxReachedStageIndex = maxReachedStageIndex,
                            lastStageIndex = totalStages - 1,
                        )
                    },
                )
            }
        }
    }
}

private fun stageStatusFromProgress(
    index: Int,
    isActivated: Boolean,
    maxReachedStageIndex: Int,
    lastStageIndex: Int,
): StageStatus {
    return when {
        !isActivated && index == 0 -> StageStatus.IN_PROGRESS
        !isActivated -> StageStatus.NOT_STARTED
        maxReachedStageIndex >= lastStageIndex -> StageStatus.COMPLETED
        index < maxReachedStageIndex -> StageStatus.COMPLETED
        index == maxReachedStageIndex -> StageStatus.IN_PROGRESS
        else -> StageStatus.NOT_STARTED
    }
}

@Composable
private fun SkippedLabel() {
    Text(
        text = stringResource(Res.string.apply_skipped),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
