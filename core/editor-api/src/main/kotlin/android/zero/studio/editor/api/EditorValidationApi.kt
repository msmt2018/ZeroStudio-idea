package android.zero.studio.editor.api

data class ValidationIssue(
    val sessionId: SessionId,
    val filePath: String?,
    val message: String,
)

interface ValidateEditorSessionsUseCase {
    suspend fun validateOpenSessions(): List<ValidationIssue>
}
