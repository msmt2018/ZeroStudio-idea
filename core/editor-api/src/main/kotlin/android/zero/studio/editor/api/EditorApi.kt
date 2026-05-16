package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

data class CursorPosition(
    val line: Int,
    val column: Int,
)

data class EditorSnapshot(
    val text: String,
    val version: Long,
    val isDirty: Boolean,
    val cursor: CursorPosition,
)

interface EditorSession {
    val filePath: String?
    val state: StateFlow<EditorSnapshot>

    suspend fun setText(text: String)
    suspend fun insert(text: String)
    suspend fun moveCursor(line: Int, column: Int)
    suspend fun save()
    suspend fun close()
}

interface EditorEngine {
    suspend fun open(filePath: String? = null, initialText: String = ""): EditorSession
}
