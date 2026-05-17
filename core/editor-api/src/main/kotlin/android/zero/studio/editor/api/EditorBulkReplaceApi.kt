package android.zero.studio.editor.api

data class BulkReplaceRequest(
    val query: String,
    val replacement: String,
    val caseSensitive: Boolean = false,
)

data class BulkReplaceResult(
    val touchedSessions: Int,
    val replacedMatches: Int,
)

interface BulkReplaceInWorkspaceUseCase {
    suspend operator fun invoke(request: BulkReplaceRequest): BulkReplaceResult
}
