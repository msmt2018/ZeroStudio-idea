package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CursorPosition
import android.zero.studio.editor.api.EditorEngine
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class InMemoryEditorEngine : EditorEngine {
    override suspend fun open(filePath: String?, initialText: String): EditorSession {
        return InMemoryEditorSession(filePath, initialText)
    }
}

class InMemoryEditorSession(
    override val filePath: String?,
    initialText: String,
) : EditorSession {
    private val closed = AtomicBoolean(false)
    private var version = 0L

    private val mutableState = MutableStateFlow(
        EditorSnapshot(
            text = initialText,
            version = version,
            isDirty = false,
            cursor = CursorPosition(0, 0),
        ),
    )

    override val state: StateFlow<EditorSnapshot> = mutableState.asStateFlow()

    override suspend fun setText(text: String) {
        ensureOpen()
        version += 1
        mutableState.value = mutableState.value.copy(
            text = text,
            version = version,
            isDirty = true,
        )
    }

    override suspend fun insert(text: String) {
        ensureOpen()
        version += 1
        mutableState.value = mutableState.value.copy(
            text = mutableState.value.text + text,
            version = version,
            isDirty = true,
        )
    }

    override suspend fun moveCursor(line: Int, column: Int) {
        ensureOpen()
        mutableState.value = mutableState.value.copy(
            cursor = CursorPosition(line = line.coerceAtLeast(0), column = column.coerceAtLeast(0)),
        )
    }

    override suspend fun save() {
        ensureOpen()
        version += 1
        mutableState.value = mutableState.value.copy(
            version = version,
            isDirty = false,
        )
    }

    override suspend fun close() {
        closed.set(true)
    }

    private fun ensureOpen() {
        check(!closed.get()) { "EditorSession is already closed." }
    }
}
