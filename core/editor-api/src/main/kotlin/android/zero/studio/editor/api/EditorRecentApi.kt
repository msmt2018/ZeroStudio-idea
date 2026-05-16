package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

interface EditorRecentFilesStore {
    val recentFiles: StateFlow<List<String>>

    suspend fun recordOpen(path: String)
    suspend fun clear()
}
