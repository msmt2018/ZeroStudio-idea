package android.zero.studio.editor.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.zero.studio.editor.impl.EditorCoreFacadeFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditorHostUiState(
    val status: String = "Idle",
    val openCount: Int = 0,
    val activeFile: String? = null,
    val lastReportPreview: String = "",
)

class EditorHostViewModel : ViewModel() {
    private val facade = EditorCoreFacadeFactory.createDefault()
    private val controller = EditorHostController(facade)

    private val _uiState = MutableStateFlow(EditorHostUiState())
    val uiState: StateFlow<EditorHostUiState> = _uiState.asStateFlow()

    fun open(path: String) = viewModelScope.launch {
        runCatching {
            val count = controller.open(path)
            _uiState.value = _uiState.value.copy(
                status = "Opened",
                openCount = count,
                activeFile = facade.coordinator.uiState.value.activeFilePath,
            )
        }.onFailure {
            _uiState.value = _uiState.value.copy(status = "Open Failed: ${it.message}")
        }
    }

    fun search(query: String) = viewModelScope.launch {
        val count = controller.searchCount(query)
        _uiState.value = _uiState.value.copy(status = "Search matches: $count")
    }

    fun replaceHelloToHi() = viewModelScope.launch {
        val count = controller.bulkReplaceHelloToHi()
        _uiState.value = _uiState.value.copy(status = "Replaced $count matches")
    }

    fun saveDirty() = viewModelScope.launch {
        val ok = controller.saveDirtyByPalette()
        _uiState.value = _uiState.value.copy(status = if (ok) "Save Dirty Triggered" else "Save command missing")
    }

    fun exportReport() = viewModelScope.launch {
        val report = controller.exportMarkdown()
        val preview = report.lineSequence().take(4).joinToString(" | ")
        _uiState.value = _uiState.value.copy(status = "Report Exported", lastReportPreview = preview)
    }
}
