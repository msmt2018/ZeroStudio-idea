package android.zero.studio.editor.api

interface EditorOpenFilesService {
    suspend fun open(path: String, languageId: String = "plaintext"): EditorSession
    suspend fun openMany(paths: List<String>, languageId: String = "plaintext"): OpenManyResult
}
