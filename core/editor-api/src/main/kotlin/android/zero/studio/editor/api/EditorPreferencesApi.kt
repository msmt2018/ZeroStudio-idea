package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

data class EditorPreferences(
    val autosaveEnabled: Boolean = true,
    val autosaveOnFocusLost: Boolean = true,
    val maxRecentFiles: Int = 20,
)

interface EditorPreferencesStore {
    val preferences: StateFlow<EditorPreferences>

    suspend fun update(transform: (EditorPreferences) -> EditorPreferences)
}
