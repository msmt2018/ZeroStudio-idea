package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.SearchOptions
import android.zero.studio.editor.api.SessionId
import android.zero.studio.editor.api.TextRange
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

    @Test
    fun `find returns case insensitive matches by default`() = runTest {
        val session = InMemoryEditorSession(SessionId("s5"), null, "Hello hello HeLLo", testMeta())
        val matches = session.find("hello")
        assertEquals(3, matches.size)
        assertEquals("Hello", matches[0].text)
    }

    @Test
    fun `find respects case sensitive option`() = runTest {
        val session = InMemoryEditorSession(SessionId("s6"), null, "Hello hello", testMeta())
        val matches = session.find("Hello", options = SearchOptions(caseSensitive = true))
        assertEquals(1, matches.size)
        assertEquals("Hello", matches[0].text)
    }

    @Test
    fun `goto updates cursor and clamps negative`() = runTest {
        val session = InMemoryEditorSession(SessionId("s7"), null, "Hello", testMeta())
        session.goTo(-1, -5)
        assertEquals(0, session.state.value.cursor.line)
        assertEquals(0, session.state.value.cursor.column)
    }

    @Test
    fun `stats returns character and line count`() = runTest {
        val session = InMemoryEditorSession(SessionId("s8"), null, "a\nb\n", testMeta())
        val stats = session.stats()
        assertEquals(4, stats.charCount)
        assertEquals(3, stats.lineCount)
    }

    @Test
    fun `operations after close should fail`() = runTest {
        val session = InMemoryEditorSession(SessionId("s9"), null, "abc", testMeta())
        session.close()

        assertFailsWith<IllegalStateException> { session.execute(EditorCommand.Insert("x")) }
        assertFailsWith<IllegalStateException> { session.find("a") }
        assertFailsWith<IllegalStateException> { session.stats() }
        assertFailsWith<IllegalStateException> { session.save() }
        assertFailsWith<IllegalStateException> { session.goTo(1, 1) }
    }
}
