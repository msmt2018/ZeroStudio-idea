package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

data class EditorUiState(
    val activeSessionId: SessionId? = null,
    val openSessionCount: Int = 0,
    val activeFilePath: String? = null,
    val isDirty: Boolean = false,
)

interface EditorCoordinator {
    val uiState: StateFlow<EditorUiState>

    suspend fun open(path: String, languageId: String = "plaintext")
    suspend fun saveActive(): Boolean
    suspend fun saveAll(): Int
}
