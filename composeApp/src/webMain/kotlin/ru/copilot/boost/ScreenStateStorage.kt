package ru.copilot.boost

import ru.copilot.boost.navigation.AppScreen

private const val SCREEN_STORAGE_KEY = "copilot.boost.currentScreen"

fun loadSavedScreen(): AppScreen {
    val screenName = runCatching { readLocalStorage(SCREEN_STORAGE_KEY) }.getOrNull()
    return AppScreen.entries.firstOrNull { it.name == screenName } ?: AppScreen.Home
}

fun saveScreen(screen: AppScreen) {
    runCatching {
        writeLocalStorage(SCREEN_STORAGE_KEY, screen.name)
    }
}
