package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

data class EditorSessionItem(
    val id: SessionId,
    val filePath: String?,
    val isDirty: Boolean,
    val isActive: Boolean,
)

interface ObserveEditorSessionsUseCase {
    val sessions: StateFlow<List<EditorSessionItem>>
}

interface CloseEditorSessionUseCase {
    suspend fun close(sessionId: SessionId)
}

interface RecentFilesUseCase {
    val recentFiles: StateFlow<List<String>>
    suspend fun clear()
}
