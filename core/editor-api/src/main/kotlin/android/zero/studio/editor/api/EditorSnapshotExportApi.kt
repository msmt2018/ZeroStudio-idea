package android.zero.studio.editor.api

data class ExportSessionSnapshot(
    val filePath: String?,
    val text: String,
    val isDirty: Boolean,
)

interface ExportEditorStateUseCase {
    suspend fun exportOpenSessions(): List<ExportSessionSnapshot>
}
