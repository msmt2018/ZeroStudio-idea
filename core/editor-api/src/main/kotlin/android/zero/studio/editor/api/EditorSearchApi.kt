package android.zero.studio.editor.api

data class SearchInWorkspaceRequest(
    val query: String,
    val caseSensitive: Boolean = false,
)

data class SessionSearchResult(
    val sessionId: SessionId,
    val filePath: String?,
    val matches: List<SearchMatch>,
)

interface SearchInWorkspaceUseCase {
    suspend operator fun invoke(request: SearchInWorkspaceRequest): List<SessionSearchResult>
}
