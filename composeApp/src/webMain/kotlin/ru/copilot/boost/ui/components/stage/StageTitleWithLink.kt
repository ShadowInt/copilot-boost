package ru.copilot.boost.ui.components.stage

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp

@Composable
fun StageTitleWithLink(
    text: String,
    linkPhrase: String,
    onLinkClick: () -> Unit,
    status: StageStatus,
    modifier: Modifier = Modifier,
) {
    val labelColor = when (status) {
        StageStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
        StageStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onSurface
        StageStatus.COMPLETED -> MaterialTheme.colorScheme.onSurface
    }
    val linkColor = MaterialTheme.colorScheme.primary
    val currentOnLinkClick by rememberUpdatedState(onLinkClick)
    val linkStart = text.indexOf(linkPhrase)
    val linkEnd = if (linkStart >= 0) linkStart + linkPhrase.length else -1
    val annotated = remember(text, linkStart, linkEnd, linkColor) {
        buildAnnotatedString {
            if (linkStart in 0..<linkEnd) {
                append(text.substring(0, linkStart))
                withLink(
                    LinkAnnotation.Clickable(
                        tag = "link",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ),
                        linkInteractionListener = { currentOnLinkClick() },
                    ),
                ) {
                    append(linkPhrase)
                }
                append(text.substring(linkEnd))
            } else {
                append(text)
            }
        }
    }
    Text(
        text = annotated,
        modifier = modifier.padding(start = 10.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.bodyMedium.copy(color = labelColor),
    )
}
