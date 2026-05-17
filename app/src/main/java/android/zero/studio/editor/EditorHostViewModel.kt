package android.zero.studio.editor.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.zero.studio.editor.api.BulkReplaceRequest
import android.zero.studio.editor.api.SearchInWorkspaceRequest
import android.zero.studio.editor.impl.EditorCoreFacade
import android.zero.studio.editor.impl.EditorCoreFacadeFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditorHostUiState(
    val status: String = "Idle",
    val openCount: Int = 0,
    val activeFile: String? = null,
)

class EditorHostViewModel(
    private val facade: EditorCoreFacade = EditorCoreFacadeFactory.createDefault(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditorHostUiState())
    val uiState: StateFlow<EditorHostUiState> = _uiState.asStateFlow()

    fun open(path: String) = viewModelScope.launch {
        runCatching {
            facade.openFilesService.open(path)
            facade.coordinator.open(path)
            val coreState = facade.coordinator.uiState.value
            _uiState.value = _uiState.value.copy(
                status = "Opened",
                openCount = coreState.openSessionCount,
                activeFile = coreState.activeFilePath,
            )
        }.onFailure {
            _uiState.value = _uiState.value.copy(status = "Open Failed: ${it.message}")
        }
    }

    fun search(query: String) = viewModelScope.launch {
        val result = facade.searchInWorkspaceUseCase(SearchInWorkspaceRequest(query))
        _uiState.value = _uiState.value.copy(status = "Search matches: ${result.sumOf { it.matches.size }}")
    }

    fun replaceHelloToHi() = viewModelScope.launch {
        val result = facade.bulkReplaceInWorkspaceUseCase(BulkReplaceRequest("hello", "hi"))
        _uiState.value = _uiState.value.copy(status = "Replaced ${result.replacedMatches} matches")
    }

    fun saveDirty() = viewModelScope.launch {
        val ok = facade.commandPaletteUseCase.execute("editor.save_dirty")
        _uiState.value = _uiState.value.copy(status = if (ok) "Save Dirty Triggered" else "Save command missing")
    }
}
