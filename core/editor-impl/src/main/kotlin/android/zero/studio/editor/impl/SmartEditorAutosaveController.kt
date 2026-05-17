package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorAutosaveController
import android.zero.studio.editor.api.EditorPreferencesStore

class SmartEditorAutosaveController(
    private val delegate: EditorAutosaveController,
    private val preferencesStore: EditorPreferencesStore,
) : EditorAutosaveController {
    override suspend fun flushDirtySessions(): Int {
        if (!preferencesStore.preferences.value.autosaveEnabled) return 0
        return delegate.flushDirtySessions()
    }

    suspend fun onFocusLost(): Int {
        if (!preferencesStore.preferences.value.autosaveOnFocusLost) return 0
        return flushDirtySessions()
    }
}
