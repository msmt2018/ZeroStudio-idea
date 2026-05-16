package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

@JvmInline
value class SessionId(val value: String)

enum class LineEnding {
    LF,
    CRLF,
}

enum class EditorKey {
    ENTER,
    TAB,
    BACKSPACE,
    DELETE,
    Z,
    Y,
    S,
    F,
    G,
}

enum class KeyModifier {
    CTRL,
    SHIFT,
    ALT,
}

data class KeyStroke(
    val key: EditorKey,
    val modifiers: Set<KeyModifier> = emptySet(),
)

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

/**
 * 编辑器视图桥接接口：用于将 EditorSession 能力绑定到具体编辑器内核（例如 Sora）。
 */
interface EditorViewAdapter {
    fun bind(session: EditorSession)
    fun unbind()
    fun applySnapshot(snapshot: EditorSnapshot)
}

interface EditorShortcutMap {
    fun resolve(shortcut: KeyStroke): EditorCommand?
    fun all(): Map<KeyStroke, EditorCommand>
}

class DefaultEditorShortcutMap : EditorShortcutMap {
    private val mappings = mapOf(
        KeyStroke(EditorKey.Z, setOf(KeyModifier.CTRL)) to EditorCommand.Undo,
        KeyStroke(EditorKey.Y, setOf(KeyModifier.CTRL)) to EditorCommand.Redo,
        KeyStroke(EditorKey.S, setOf(KeyModifier.CTRL)) to EditorCommand.Save,
    )

    override fun resolve(shortcut: KeyStroke): EditorCommand? = mappings[shortcut]

    override fun all(): Map<KeyStroke, EditorCommand> = mappings
}
