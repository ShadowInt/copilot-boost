package ru.copilot.boost

import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun platformReadFileAsText(file: File, onRead: (String) -> Unit, onError: () -> Unit) {
    val reader = FileReader()
    reader.onload = {
        onRead(reader.result?.toString().orEmpty())
        null
    }
    reader.onerror = {
        onError()
        null
    }
    reader.readAsText(file)
}

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun platformSetBeforeUnloadReturnValue(event: Event, message: String): Unit =
    js("event.returnValue = message")

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun platformWriteTextToClipboard(text: String) {
    js("window.navigator && window.navigator.clipboard && window.navigator.clipboard.writeText(text)")
}
