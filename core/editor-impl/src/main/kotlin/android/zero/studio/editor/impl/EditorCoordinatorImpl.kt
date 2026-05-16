package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCoordinator
import android.zero.studio.editor.api.EditorUiState
import android.zero.studio.editor.api.OpenEditorRequest
import android.zero.studio.editor.api.OpenEditorUseCase
import android.zero.studio.editor.api.SaveEditorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditorCoordinatorImpl(
    private val workspace: PersistentEditorWorkspace,
    private val openEditorUseCase: OpenEditorUseCase,
    private val saveEditorUseCase: SaveEditorUseCase,
) : EditorCoordinator {
    private val _uiState = MutableStateFlow(EditorUiState())
    override val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    override suspend fun open(path: String, languageId: String) {
        openEditorUseCase(OpenEditorRequest(path, languageId))
        publishState()
    }

    override suspend fun saveActive(): Boolean {
        val result = saveEditorUseCase.saveActive()
        publishState()
        return result
    }

    override suspend fun saveAll(): Int {
        val count = saveEditorUseCase.saveAll()
        publishState()
        return count
    }

    private fun publishState() {
        val activeId = workspace.activeSessionId.value
        val active = workspace.sessions.value.firstOrNull { it.id == activeId }
        _uiState.value = EditorUiState(
            activeSessionId = activeId,
            openSessionCount = workspace.sessions.value.size,
            activeFilePath = active?.filePath,
            isDirty = active?.state?.value?.isDirty == true,
        )
    }
}
