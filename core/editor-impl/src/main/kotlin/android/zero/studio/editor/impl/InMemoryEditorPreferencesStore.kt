package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorPreferences
import android.zero.studio.editor.api.EditorPreferencesStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryEditorPreferencesStore : EditorPreferencesStore {
    private val _preferences = MutableStateFlow(EditorPreferences())
    override val preferences: StateFlow<EditorPreferences> = _preferences.asStateFlow()

    override suspend fun update(transform: (EditorPreferences) -> EditorPreferences) {
        _preferences.value = transform(_preferences.value)
    }
}
