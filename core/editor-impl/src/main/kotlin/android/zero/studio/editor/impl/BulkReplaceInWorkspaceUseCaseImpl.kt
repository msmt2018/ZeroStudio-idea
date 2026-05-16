package android.zero.studio.editor.impl

import android.zero.studio.editor.api.BulkReplaceInWorkspaceUseCase
import android.zero.studio.editor.api.BulkReplaceRequest
import android.zero.studio.editor.api.BulkReplaceResult
import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.SearchOptions

class BulkReplaceInWorkspaceUseCaseImpl(
    private val workspace: EditorWorkspace,
) : BulkReplaceInWorkspaceUseCase {
    override suspend fun invoke(request: BulkReplaceRequest): BulkReplaceResult {
        if (request.query.isBlank()) return BulkReplaceResult(0, 0)

        var touchedSessions = 0
        var replacedMatches = 0

        val options = SearchOptions(caseSensitive = request.caseSensitive)
        workspace.sessions.value.forEach { session ->
            val source = session.state.value.text
            val matches = session.find(request.query, options)
            if (matches.isEmpty()) return@forEach

            val next = if (request.caseSensitive) {
                source.replace(request.query, request.replacement)
            } else {
                replaceIgnoringCase(source, request.query, request.replacement)
            }

            session.execute(EditorCommand.SetText(next))
            touchedSessions += 1
            replacedMatches += matches.size
        }

        return BulkReplaceResult(touchedSessions, replacedMatches)
    }

    private fun replaceIgnoringCase(source: String, query: String, replacement: String): String {
        val hay = source.lowercase()
        val needle = query.lowercase()
        val sb = StringBuilder()
        var from = 0
        while (from < source.length) {
            val idx = hay.indexOf(needle, from)
            if (idx < 0) {
                sb.append(source.substring(from))
                break
            }
            sb.append(source.substring(from, idx))
            sb.append(replacement)
            from = idx + query.length
        }
        return sb.toString()
    }
}
