package ru.copilot.boost.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.copilot.boost.ui.components.stage.StatusColors

enum class SnackbarTone {
    Success,
    Warning,
    Error,
}

private data class AppSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val tone: SnackbarTone = SnackbarTone.Success,
) : SnackbarVisuals

suspend fun SnackbarHostState.showAppSnackbar(
    message: String,
    tone: SnackbarTone,
    duration: SnackbarDuration = SnackbarDuration.Short,
) {
    currentSnackbarData?.dismiss()
    showSnackbar(
        AppSnackbarVisuals(
            message = message,
            duration = duration,
            tone = tone,
        ),
    )
}

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { snackbarData ->
        AppSnackbar(snackbarData = snackbarData)
    }
}

@Composable
private fun AppSnackbar(snackbarData: SnackbarData) {
    val visuals = snackbarData.visuals as? AppSnackbarVisuals
    val tone = visuals?.tone ?: SnackbarTone.Success
    val containerColor = when (tone) {
        SnackbarTone.Success -> StatusColors.completed
        SnackbarTone.Warning -> StatusColors.inProgress
        SnackbarTone.Error -> StatusColors.error
    }
    val contentColor = when (tone) {
        SnackbarTone.Warning -> Color(0xFF1F1F1F)
        SnackbarTone.Success, SnackbarTone.Error -> Color.White
    }
    val icon = when (tone) {
        SnackbarTone.Success -> Icons.Filled.CheckCircle
        SnackbarTone.Warning -> Icons.Filled.Warning
        SnackbarTone.Error -> Icons.Filled.Error
    }

    Snackbar(
        modifier = Modifier.widthIn(max = 280.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = contentColor,
        dismissActionContentColor = contentColor,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
            )
            Text(
                text = snackbarData.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
