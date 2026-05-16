package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SoraEditorBridgeTest {

    @Test
    fun `bind should render initial snapshot`() = runTest {
        val rendered = mutableListOf<String>()
        val bridge = SoraEditorBridge(
            scope = CoroutineScope(Job() + Dispatchers.Unconfined),
            renderer = EditorRenderer { rendered += it.text },
        )
        val session = InMemoryEditorSession(SessionId("b1"), null, "hello", testMeta())

        bridge.bind(session)

        assertTrue(rendered.isNotEmpty())
        assertEquals("hello", rendered.first())
    }

    @Test
    fun `text change should dispatch SetText to session`() = runTest {
        val bridge = SoraEditorBridge(
            scope = CoroutineScope(Job() + Dispatchers.Unconfined),
            renderer = NoopEditorRenderer,
        )
        val session = InMemoryEditorSession(SessionId("b2"), null, "a", testMeta())
        bridge.bind(session)

        bridge.onTextChanged("xyz")

        assertEquals("xyz", session.state.value.text)
    }

    @Test
    fun `cursor move and save should dispatch to session`() = runTest {
        val bridge = SoraEditorBridge(
            scope = CoroutineScope(Job() + Dispatchers.Unconfined),
            renderer = NoopEditorRenderer,
        )
        val session = InMemoryEditorSession(SessionId("b3"), null, "a", testMeta())
        bridge.bind(session)

        bridge.onCursorMoved(3, 5)
        session.execute(EditorCommand.SetText("abc"))
        bridge.onSaveRequested()

        assertEquals(3, session.state.value.cursor.line)
        assertEquals(5, session.state.value.cursor.column)
        assertTrue(!session.state.value.isDirty)
    }
}
