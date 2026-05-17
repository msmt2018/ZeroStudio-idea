package android.zero.studio.editor.api

data class OpenEditorRequest(
    val path: String,
    val languageId: String = "plaintext",
)

interface OpenEditorUseCase {
    suspend operator fun invoke(request: OpenEditorRequest): EditorSession
}

interface SaveEditorUseCase {
    suspend fun saveActive(): Boolean
    suspend fun saveAll(): Int
}
