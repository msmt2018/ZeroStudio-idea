package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EditorRecoveryUseCasesImplTest {

    @Test
    fun `capture should store all open sessions`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val s1 = workspace.newSession(filePath = "/a.txt", initialText = "a")
        val s2 = workspace.newSession(filePath = "/b.txt", initialText = "b")
        s1.execute(EditorCommand.SetText("a2"))

        val store = InMemoryEditorRecoveryStore()
        val capture = CaptureRecoveryUseCaseImpl(workspace, store)

        assertEquals(2, capture.captureAllOpenSessions())
        assertEquals(2, store.snapshots.value.size)
        assertEquals("a2", store.snapshots.value.first { it.filePath == "/a.txt" }.text)
        assertEquals(s2.id, store.snapshots.value.first { it.filePath == "/b.txt" }.sessionId)
    }

    @Test
    fun `restore should recreate sessions from snapshots`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val store = InMemoryEditorRecoveryStore()
        store.save(android.zero.studio.editor.api.RecoverySnapshot(android.zero.studio.editor.api.SessionId("x1"), "/x", "hello"))
        store.save(android.zero.studio.editor.api.RecoverySnapshot(android.zero.studio.editor.api.SessionId("x2"), null, "scratch"))

        val restore = RestoreRecoveryUseCaseImpl(workspace, store)
        val count = restore.restoreAll()

        assertEquals(2, count)
        assertEquals(2, workspace.sessions.value.size)
        assertEquals("hello", workspace.sessions.value.first { it.filePath == "/x" }.state.value.text)
    }
}
