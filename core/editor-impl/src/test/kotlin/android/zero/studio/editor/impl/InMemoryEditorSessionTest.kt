package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.SessionId
import android.zero.studio.editor.api.TextRange
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InMemoryEditorSessionTest {

    @Test
    fun `set text marks dirty and increments version`() = runTest {
        val session = InMemoryEditorSession(
            id = SessionId("s1"),
            filePath = null,
            initialText = "a",
            meta = testMeta(),
        )

        session.execute(EditorCommand.SetText("abc"))

        val state = session.state.value
        assertEquals("abc", state.text)
        assertTrue(state.isDirty)
        assertEquals(1L, state.version)
        assertTrue(state.canUndo)
    }

    @Test
    fun `undo redo roundtrip works`() = runTest {
        val session = InMemoryEditorSession(SessionId("s2"), null, "hello", testMeta())
        session.execute(EditorCommand.Insert(" world"))
        assertEquals("hello world", session.state.value.text)

        session.execute(EditorCommand.Undo)
        assertEquals("hello", session.state.value.text)
        assertTrue(session.state.value.canRedo)

        session.execute(EditorCommand.Redo)
        assertEquals("hello world", session.state.value.text)
    }

    @Test
    fun `replace range applies safely`() = runTest {
        val session = InMemoryEditorSession(SessionId("s3"), null, "abcdef", testMeta())
        session.execute(EditorCommand.ReplaceRange(TextRange(2, 4), "ZZ"))
        assertEquals("abZZef", session.state.value.text)
    }

    @Test
    fun `save clears dirty flag`() = runTest {
        val session = InMemoryEditorSession(SessionId("s4"), null, "x", testMeta())
        session.execute(EditorCommand.SetText("y"))
        assertTrue(session.state.value.isDirty)

        session.save()
        assertFalse(session.state.value.isDirty)
    }
}
