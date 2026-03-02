package ru.copilot.boost

import kotlinx.browser.window
import org.w3c.dom.DragEvent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import ru.copilot.boost.model.UploadedFileData

fun observeGlobalFileDrop(
    onDragStateChanged: (Boolean) -> Unit,
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
): () -> Unit {
    val dragOverListener: (Event) -> Unit = { event ->
        val dragEvent = event as DragEvent
        dragEvent.preventDefault()
        onDragStateChanged(true)
    }

    val dragLeaveListener: (Event) -> Unit = { event ->
        event.preventDefault()
        onDragStateChanged(false)
    }

    val dropListener: (Event) -> Unit = { event ->
        val dragEvent = event as DragEvent
        dragEvent.preventDefault()
        onDragStateChanged(false)
        val file = dragEvent.dataTransfer?.files?.item(0)
        if (file != null) {
            if (isAllowedClientCfgFile(file.name)) {
                platformReadFileAsText(file) { content ->
                    onFileSelected(UploadedFileData(name = file.name, content = content))
                }
            } else {
                onInvalidFile()
            }
        }
    }

    window.document.addEventListener("dragover", dragOverListener)
    window.document.addEventListener("dragleave", dragLeaveListener)
    window.document.addEventListener("drop", dropListener)

    return {
        window.document.removeEventListener("dragover", dragOverListener)
        window.document.removeEventListener("dragleave", dragLeaveListener)
        window.document.removeEventListener("drop", dropListener)
    }
}

fun openFilePicker(
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
) {
    val input = window.document.createElement("input") as HTMLInputElement
    input.type = "file"
    input.accept = ".cfg"
    input.style.display = "none"

    input.onchange = {
        val file = input.files?.item(0)
        if (file != null) {
            if (isAllowedClientCfgFile(file.name)) {
                platformReadFileAsText(file) { content ->
                    onFileSelected(UploadedFileData(name = file.name, content = content))
                }
            } else {
                onInvalidFile()
            }
        }
        input.remove()
        null
    }

    window.document.body?.appendChild(input)
    input.click()
}

fun downloadCfgFile(fileName: String, content: String) {
    val base64Content = window.btoa(content)
    val anchor = window.document.createElement("a") as HTMLAnchorElement
    anchor.href = "data:text/plain;base64,$base64Content"
    anchor.download = fileName
    window.document.body?.appendChild(anchor)
    anchor.click()
    anchor.remove()
}

fun copyTextToClipboard(text: String) {
    platformWriteTextToClipboard(text)
}

fun observePageUnloadWarning(message: String): () -> Unit {
    val beforeUnloadListener: (Event) -> Unit = { event ->
        event.preventDefault()
        platformSetBeforeUnloadReturnValue(event, message)
    }
    window.addEventListener("beforeunload", beforeUnloadListener)
    return {
        window.removeEventListener("beforeunload", beforeUnloadListener)
    }
}

private fun isAllowedClientCfgFile(fileName: String): Boolean = fileName == "client.cfg"

internal expect fun platformReadFileAsText(file: File, onRead: (String) -> Unit)

internal expect fun platformSetBeforeUnloadReturnValue(event: Event, message: String)

internal expect fun platformWriteTextToClipboard(text: String)
