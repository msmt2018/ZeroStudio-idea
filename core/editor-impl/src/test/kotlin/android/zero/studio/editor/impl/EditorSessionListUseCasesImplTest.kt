package android.zero.studio.editor.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EditorSessionListUseCasesImplTest {

    @Test
    fun `observe sessions should emit active and dirty flags`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val observer = ObserveEditorSessionsUseCaseImpl(
            workspace,
            scope = CoroutineScope(Job() + Dispatchers.Unconfined),
        )

        val s1 = workspace.newSession(filePath = "/a.txt", initialText = "a")
        val s2 = workspace.newSession(filePath = "/b.txt", initialText = "b")
        s1.execute(android.zero.studio.editor.api.EditorCommand.SetText("a1"))
        workspace.activate(s1.id)

        val items = observer.sessions.value
        assertEquals(2, items.size)
        val a = items.first { it.filePath == "/a.txt" }
        val b = items.first { it.filePath == "/b.txt" }
        assertTrue(a.isDirty)
        assertTrue(a.isActive)
        assertFalse(b.isActive)

        // keep var used
        assertEquals(s2.filePath, b.filePath)
    }

    @Test
    fun `close session usecase should remove session`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val s = workspace.newSession(filePath = "/a.txt", initialText = "a")
        val closeUseCase = CloseEditorSessionUseCaseImpl(workspace)

        closeUseCase.close(s.id)

        assertEquals(0, workspace.sessions.value.size)
    }

    @Test
    fun `recent files usecase should expose and clear list`() = runTest {
        val store = InMemoryRecentFilesStore(maxSize = 10)
        val useCase = RecentFilesUseCaseImpl(store)

        store.recordOpen("/x")
        store.recordOpen("/y")
        assertEquals(listOf("/y", "/x"), useCase.recentFiles.value)

        useCase.clear()
        assertTrue(useCase.recentFiles.value.isEmpty())
    }
}
