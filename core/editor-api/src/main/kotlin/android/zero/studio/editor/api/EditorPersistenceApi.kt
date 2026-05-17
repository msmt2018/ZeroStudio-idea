package android.zero.studio.editor.api

/**
 * 文档持久化抽象：用于在 editor-impl 层实现 file IO。
 */
interface EditorDocumentStore {
    suspend fun load(path: String, charset: String = "UTF-8"): String
    suspend fun save(path: String, content: String, charset: String = "UTF-8")
}

interface EditorSessionFactory {
    suspend fun openFromPath(path: String, languageId: String = "plaintext"): EditorSession
}
