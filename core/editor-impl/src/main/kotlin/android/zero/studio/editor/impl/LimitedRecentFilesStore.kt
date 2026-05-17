package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorPreferencesStore
import android.zero.studio.editor.api.EditorRecentFilesStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LimitedRecentFilesStore(
    private val preferencesStore: EditorPreferencesStore,
) : EditorRecentFilesStore {
    private val _recentFiles = MutableStateFlow<List<String>>(emptyList())
    override val recentFiles: StateFlow<List<String>> = _recentFiles.asStateFlow()

    override suspend fun recordOpen(path: String) {
        val normalized = path.trim()
        if (normalized.isBlank()) return

        val maxSize = preferencesStore.preferences.value.maxRecentFiles
        val next = buildList {
            add(normalized)
            addAll(_recentFiles.value.filterNot { it == normalized })
        }.take(maxSize)
        _recentFiles.value = next
    }

    override suspend fun clear() {
        _recentFiles.value = emptyList()
    }
}
