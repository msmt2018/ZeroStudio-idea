package android.zero.studio.editor.editor

import android.zero.studio.editor.api.BulkReplaceRequest
import android.zero.studio.editor.api.SearchInWorkspaceRequest
import android.zero.studio.editor.impl.EditorCoreFacade

/**
 * App 层控制器：封装高频 UI 动作，降低 ViewModel 复杂度。
 */
class EditorHostController(
    private val facade: EditorCoreFacade,
) {
    suspend fun open(path: String, languageId: String = "plaintext"): Int {
        facade.openFilesService.open(path, languageId)
        facade.coordinator.open(path, languageId)
        return facade.coordinator.uiState.value.openSessionCount
    }

    suspend fun searchCount(query: String): Int {
        val result = facade.searchInWorkspaceUseCase(SearchInWorkspaceRequest(query))
        return result.sumOf { it.matches.size }
    }

    suspend fun bulkReplaceHelloToHi(): Int {
        val result = facade.bulkReplaceInWorkspaceUseCase(BulkReplaceRequest("hello", "hi"))
        return result.replacedMatches
    }

    suspend fun saveDirtyByPalette(): Boolean {
        return facade.commandPaletteUseCase.execute("editor.save_dirty")
    }

    suspend fun exportMarkdown(): String {
        val builder = android.zero.studio.editor.impl.EditorSummaryReportBuilder(
            exportUseCase = facade.exportEditorStateUseCase,
            metricsUseCase = facade.metricsUseCase,
        )
        return builder.buildMarkdownReport()
    }
}
