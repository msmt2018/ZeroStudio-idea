package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCoordinator
import android.zero.studio.editor.api.RecentFilesUseCase
import android.zero.studio.editor.api.ObserveEditorSessionsUseCase

data class EditorDashboardState(
    val openSessionCount: Int,
    val activeFilePath: String?,
    val recentFiles: List<String>,
    val sessionTitles: List<String>,
)

class EditorDashboardAssembler(
    private val coordinator: EditorCoordinator,
    private val sessionsUseCase: ObserveEditorSessionsUseCase,
    private val recentFilesUseCase: RecentFilesUseCase,
) {
    fun snapshot(): EditorDashboardState {
        val ui = coordinator.uiState.value
        val sessionTitles = sessionsUseCase.sessions.value.map { it.filePath ?: "<scratch>" }
        return EditorDashboardState(
            openSessionCount = ui.openSessionCount,
            activeFilePath = ui.activeFilePath,
            recentFiles = recentFilesUseCase.recentFiles.value,
            sessionTitles = sessionTitles,
        )
    }
}
