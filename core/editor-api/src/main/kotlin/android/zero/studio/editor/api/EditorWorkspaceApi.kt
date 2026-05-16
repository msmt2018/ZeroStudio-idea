package android.zero.studio.editor.api

interface EditorWorkspaceManager {
    suspend fun openFile(path: String, languageId: String = "plaintext"): EditorSession
    suspend fun newScratch(initialText: String = "", languageId: String = "plaintext"): EditorSession
    suspend fun saveActive(): Boolean
    suspend fun saveAll(): Int
}
