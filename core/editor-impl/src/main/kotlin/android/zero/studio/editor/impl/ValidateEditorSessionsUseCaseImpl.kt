package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.ValidateEditorSessionsUseCase
import android.zero.studio.editor.api.ValidationIssue

class ValidateEditorSessionsUseCaseImpl(
    private val workspace: EditorWorkspace,
) : ValidateEditorSessionsUseCase {
    override suspend fun validateOpenSessions(): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()

        workspace.sessions.value.forEach { session ->
            val snapshot = session.state.value
            if (snapshot.text.contains("\t")) {
                issues += ValidationIssue(
                    sessionId = session.id,
                    filePath = session.filePath,
                    message = "Contains TAB characters; consider spaces for consistency.",
                )
            }
            if (snapshot.text.length > 100_000) {
                issues += ValidationIssue(
                    sessionId = session.id,
                    filePath = session.filePath,
                    message = "Large document detected (>100k chars); enable performance mode.",
                )
            }
            if (snapshot.documentMeta.charset.isBlank()) {
                issues += ValidationIssue(
                    sessionId = session.id,
                    filePath = session.filePath,
                    message = "Document charset is blank.",
                )
            }
        }
        return issues
    }
}
