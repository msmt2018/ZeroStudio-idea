package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CursorPosition
import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.EditorDocumentMeta
import android.zero.studio.editor.api.EditorEngine
import android.zero.studio.editor.api.EditorSelection
import android.zero.studio.editor.api.EditorStats
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSnapshot
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.LineEnding
import android.zero.studio.editor.api.SessionId
import android.zero.studio.editor.api.SearchMatch
import android.zero.studio.editor.api.SearchOptions
import android.zero.studio.editor.api.TextRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class InMemoryEditorEngine : EditorEngine {
    override suspend fun open(
        filePath: String?,
        initialText: String,
        languageId: String,
        charset: String,
        lineEnding: LineEnding,
    ): EditorSession {
        return InMemoryEditorSession(
            id = SessionId(UUID.randomUUID().toString()),
            filePath = filePath,
            initialText = initialText,
            meta = EditorDocumentMeta(filePath, languageId, charset, lineEnding),
        )
    }
}

class InMemoryEditorWorkspace(
    private val engine: EditorEngine = InMemoryEditorEngine(),
) : EditorWorkspace {
    private val _sessions = MutableStateFlow<List<EditorSession>>(emptyList())
    override val sessions: StateFlow<List<EditorSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<SessionId?>(null)
    override val activeSessionId: StateFlow<SessionId?> = _activeSessionId.asStateFlow()

    override suspend fun newSession(filePath: String?, initialText: String, languageId: String): EditorSession {
        val session = engine.open(filePath, initialText, languageId)
        _sessions.value = _sessions.value + session
        _activeSessionId.value = session.id
        return session
    }

    override suspend fun activate(sessionId: SessionId) {
        if (_sessions.value.any { it.id == sessionId }) {
            _activeSessionId.value = sessionId
        }
    }

    override suspend fun close(sessionId: SessionId) {
        val session = _sessions.value.find { it.id == sessionId } ?: return
        session.close()
        _sessions.value = _sessions.value.filterNot { it.id == sessionId }
        if (_activeSessionId.value == sessionId) {
            _activeSessionId.value = _sessions.value.lastOrNull()?.id
        }
    }
}

class InMemoryEditorSession(
    override val id: SessionId,
    override val filePath: String?,
    initialText: String,
    private val meta: EditorDocumentMeta,
) : EditorSession {
    private val closed = AtomicBoolean(false)
    private var version = 0L
    private val undoStack = ArrayDeque<String>()
    private val redoStack = ArrayDeque<String>()

    private val mutableState = MutableStateFlow(
        EditorSnapshot(
            text = initialText,
            version = version,
            isDirty = false,
            cursor = CursorPosition(0, 0),
            selection = EditorSelection(emptyList()),
            canUndo = false,
            canRedo = false,
            documentMeta = meta,
        ),
    )

    override val state: StateFlow<EditorSnapshot> = mutableState.asStateFlow()

    override suspend fun execute(command: EditorCommand) {
        ensureOpen()
        when (command) {
            is EditorCommand.SetText -> applyTextChange(command.text)
            is EditorCommand.Insert -> applyTextChange(state.value.text + command.text)
            is EditorCommand.ReplaceRange -> applyTextChange(replaceRange(command.range, command.text))
            is EditorCommand.MoveCursor -> updateCursor(command.line, command.column)
            is EditorCommand.SetSelection -> {
                mutableState.value = state.value.copy(selection = command.selection)
            }
            EditorCommand.Undo -> undo()
            EditorCommand.Redo -> redo()
            EditorCommand.Save -> save()
        }
    }


    override suspend fun goTo(line: Int, column: Int) {
        ensureOpen()
        updateCursor(line, column)
    }

    override suspend fun find(query: String, options: SearchOptions): List<SearchMatch> {
        ensureOpen()
        if (query.isEmpty()) return emptyList()

        val source = state.value.text
        val haystack = if (options.caseSensitive) source else source.lowercase()
        val needle = if (options.caseSensitive) query else query.lowercase()

        var from = 0
        val matches = mutableListOf<SearchMatch>()
        while (from < haystack.length) {
            val idx = haystack.indexOf(needle, startIndex = from)
            if (idx < 0) break
            val end = idx + needle.length
            matches += SearchMatch(TextRange(idx, end), source.substring(idx, end))
            from = end
        }
        return matches
    }

    override suspend fun stats(): EditorStats {
        ensureOpen()
        val t = state.value.text
        val lineCount = if (t.isEmpty()) 1 else t.count { it == "\n"[0] } + 1
        return EditorStats(charCount = t.length, lineCount = lineCount)
    }

    override suspend fun save() {
        ensureOpen()
        version += 1
        mutableState.value = state.value.copy(version = version, isDirty = false)
    }

    override suspend fun close() {
        closed.set(true)
    }

    private fun applyTextChange(nextText: String) {
        undoStack.addLast(state.value.text)
        redoStack.clear()
        version += 1
        mutableState.value = state.value.copy(
            text = nextText,
            version = version,
            isDirty = true,
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty(),
        )
    }

    private fun replaceRange(range: TextRange, text: String): String {
        val source = state.value.text
        val start = range.start.coerceIn(0, source.length)
        val end = range.end.coerceIn(start, source.length)
        return source.substring(0, start) + text + source.substring(end)
    }

    private fun undo() {
        if (undoStack.isEmpty()) return
        val previous = undoStack.removeLast()
        redoStack.addLast(state.value.text)
        version += 1
        mutableState.value = state.value.copy(
            text = previous,
            version = version,
            isDirty = true,
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty(),
        )
    }

    private fun redo() {
        if (redoStack.isEmpty()) return
        val next = redoStack.removeLast()
        undoStack.addLast(state.value.text)
        version += 1
        mutableState.value = state.value.copy(
            text = next,
            version = version,
            isDirty = true,
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty(),
        )
    }

    private fun updateCursor(line: Int, column: Int) {
        mutableState.value = state.value.copy(
            cursor = CursorPosition(line.coerceAtLeast(0), column.coerceAtLeast(0)),
        )
    }

    private fun ensureOpen() {
        check(!closed.get()) { "EditorSession is already closed." }
    }
}
