package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

@JvmInline
value class SessionId(val value: String)

enum class LineEnding {
    LF,
    CRLF,
}

data class CursorPosition(
    val line: Int,
    val column: Int,
)

data class TextRange(
    val start: Int,
    val end: Int,
)

data class EditorSelection(
    val ranges: List<TextRange>,
)

data class EditorDocumentMeta(
    val filePath: String?,
    val languageId: String,
    val charset: String,
    val lineEnding: LineEnding,
)

data class EditorStats(
    val charCount: Int,
    val lineCount: Int,
)

data class EditorSnapshot(
    val text: String,
    val version: Long,
    val isDirty: Boolean,
    val cursor: CursorPosition,
    val selection: EditorSelection,
    val canUndo: Boolean,
    val canRedo: Boolean,
    val documentMeta: EditorDocumentMeta,
)

data class SearchOptions(
    val caseSensitive: Boolean = false,
)

data class SearchMatch(
    val range: TextRange,
    val text: String,
)

sealed interface EditorCommand {
    data class SetText(val text: String) : EditorCommand
    data class Insert(val text: String) : EditorCommand
    data class ReplaceRange(val range: TextRange, val text: String) : EditorCommand
    data class MoveCursor(val line: Int, val column: Int) : EditorCommand
    data class SetSelection(val selection: EditorSelection) : EditorCommand
    data object Undo : EditorCommand
    data object Redo : EditorCommand
    data object Save : EditorCommand
}

interface EditorSession {
    val id: SessionId
    val filePath: String?
    val state: StateFlow<EditorSnapshot>

    suspend fun execute(command: EditorCommand)
    suspend fun goTo(line: Int, column: Int)
    suspend fun find(query: String, options: SearchOptions = SearchOptions()): List<SearchMatch>
    suspend fun stats(): EditorStats
    suspend fun save()
    suspend fun close()
}

interface EditorEngine {
    suspend fun open(
        filePath: String? = null,
        initialText: String = "",
        languageId: String = "plaintext",
        charset: String = "UTF-8",
        lineEnding: LineEnding = LineEnding.LF,
    ): EditorSession
}

interface EditorWorkspace {
    val sessions: StateFlow<List<EditorSession>>
    val activeSessionId: StateFlow<SessionId?>

    suspend fun newSession(
        filePath: String? = null,
        initialText: String = "",
        languageId: String = "plaintext",
    ): EditorSession

    suspend fun activate(sessionId: SessionId)
    suspend fun close(sessionId: SessionId)
}
