package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.SearchInWorkspaceRequest
import android.zero.studio.editor.api.SearchInWorkspaceUseCase
import android.zero.studio.editor.api.SearchOptions
import android.zero.studio.editor.api.SessionSearchResult

class WorkspaceSearchUseCaseImpl(
    private val workspace: EditorWorkspace,
) : SearchInWorkspaceUseCase {
    override suspend fun invoke(request: SearchInWorkspaceRequest): List<SessionSearchResult> {
        if (request.query.isBlank()) return emptyList()

        val options = SearchOptions(caseSensitive = request.caseSensitive)
        return workspace.sessions.value.mapNotNull { session ->
            val matches = session.find(request.query, options)
            if (matches.isEmpty()) {
                null
            } else {
                SessionSearchResult(
                    sessionId = session.id,
                    filePath = session.filePath,
                    matches = matches,
                )
            }
        }
    }
}
