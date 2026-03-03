package ru.copilot.boost

import kotlinx.browser.window
import org.w3c.dom.DragEvent
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import ru.copilot.boost.model.UploadedFileData

private var skipNextBeforeUnloadWarning = false

fun observeGlobalFileDrop(
    onDragStateChanged: (Boolean) -> Unit,
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
    onReadError: () -> Unit,
): () -> Unit {
    var dragCounter = 0

    val dragOverListener: (Event) -> Unit = { event ->
        event.preventDefault()
    }

    val dragEnterListener: (Event) -> Unit = { event ->
        event.preventDefault()
        dragCounter++
        if (dragCounter == 1) onDragStateChanged(true)
    }

    val dragLeaveListener: (Event) -> Unit = { event ->
        event.preventDefault()
        dragCounter--
        if (dragCounter <= 0) {
            dragCounter = 0
            onDragStateChanged(false)
        }
    }

    val dropListener: (Event) -> Unit = { event ->
        val dragEvent = event as DragEvent
        dragEvent.preventDefault()
        dragCounter = 0
        onDragStateChanged(false)
        val file = dragEvent.dataTransfer?.files?.item(0)
        if (file != null) {
            if (isAllowedClientCfgFile(file.name)) {
                platformReadFileAsText(
                    file = file,
                    onRead = { content ->
                        onFileSelected(UploadedFileData(name = file.name, content = content))
                    },
                    onError = onReadError,
                )
            } else {
                onInvalidFile()
            }
        }
    }

    window.document.addEventListener("dragover", dragOverListener)
    window.document.addEventListener("dragenter", dragEnterListener)
    window.document.addEventListener("dragleave", dragLeaveListener)
    window.document.addEventListener("drop", dropListener)

    return {
        window.document.removeEventListener("dragover", dragOverListener)
        window.document.removeEventListener("dragenter", dragEnterListener)
        window.document.removeEventListener("dragleave", dragLeaveListener)
        window.document.removeEventListener("drop", dropListener)
    }
}

fun openFilePicker(
    onFileSelected: (UploadedFileData) -> Unit,
    onInvalidFile: () -> Unit,
    onReadError: () -> Unit,
) {
    val input = window.document.createElement("input") as HTMLInputElement
    input.type = "file"
    input.accept = ".cfg"
    input.style.display = "none"

    input.onchange = {
        val file = input.files?.item(0)
        if (file != null) {
            if (isAllowedClientCfgFile(file.name)) {
                platformReadFileAsText(
                    file = file,
                    onRead = { content ->
                        onFileSelected(UploadedFileData(name = file.name, content = content))
                    },
                    onError = onReadError,
                )
            } else {
                onInvalidFile()
            }
        }
        input.remove()
        null
    }

    val body = window.document.body ?: return
    body.appendChild(input)
    input.click()
}

fun downloadCfgFile(fileName: String, content: String) {
    val body = window.document.body ?: return
    val base64Content = window.btoa(content)
    val anchor = window.document.createElement("a") as HTMLAnchorElement
    anchor.href = "data:text/plain;base64,$base64Content"
    anchor.download = fileName
    body.appendChild(anchor)
    anchor.click()
    anchor.remove()
}

fun copyTextToClipboard(text: String) {
    platformWriteTextToClipboard(text)
}

fun suppressNextUnloadWarning(durationMs: Int = 1500) {
    skipNextBeforeUnloadWarning = true
    window.setTimeout(
        handler = {
            skipNextBeforeUnloadWarning = false
            null
        },
        timeout = durationMs,
    )
}

fun observePageUnloadWarning(message: String): () -> Unit {
    val beforeUnloadListener: (Event) -> Unit = { event ->
        if (skipNextBeforeUnloadWarning) {
            skipNextBeforeUnloadWarning = false
        } else {
            event.preventDefault()
            platformSetBeforeUnloadReturnValue(event, message)
        }
    }
    window.addEventListener("beforeunload", beforeUnloadListener)
    return {
        window.removeEventListener("beforeunload", beforeUnloadListener)
    }
}

private fun isAllowedClientCfgFile(fileName: String): Boolean =
    fileName.equals("client.cfg", ignoreCase = true)

internal expect fun platformReadFileAsText(file: File, onRead: (String) -> Unit, onError: () -> Unit)

internal expect fun platformSetBeforeUnloadReturnValue(event: Event, message: String)

internal expect fun platformWriteTextToClipboard(text: String)
