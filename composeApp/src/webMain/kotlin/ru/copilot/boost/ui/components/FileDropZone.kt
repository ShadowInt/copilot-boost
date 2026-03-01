package ru.copilot.boost.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Переиспользуемая зона drag and drop для загрузки файлов.
 *
 * @param isDragging true, когда файл перетаскивают над областью
 * @param idleMessage текст, когда перетаскивания нет
 * @param dragMessage текст во время перетаскивания
 * @param pickButtonText подпись кнопки выбора файла
 * @param onPickFileClick вызывается при нажатии на кнопку выбора файла
 * @param errorMessage опциональное сообщение об ошибке под кнопкой
 */
@Composable
fun FileDropZone(
    isDragging: Boolean,
    idleMessage: String,
    dragMessage: String,
    pickButtonText: String = "Выберите файл",
    onPickFileClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val dropZoneShape = RoundedCornerShape(16.dp)
    val borderColor = if (isDragging) MaterialTheme.colorScheme.primary else Color.Gray
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val cornerRadius = 16.dp.toPx()
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                    ),
                    cornerRadius = CornerRadius(cornerRadius),
                )
            }
            .clip(dropZoneShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (isDragging) dragMessage else idleMessage,
                style = MaterialTheme.typography.headlineSmall,
            )
            if (!isDragging) {
                Text(
                    text = "ИЛИ",
                    modifier = Modifier.padding(top = 10.dp),
                )
                Button(
                    onClick = onPickFileClick,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(pickButtonText)
                }
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }
    }
}
