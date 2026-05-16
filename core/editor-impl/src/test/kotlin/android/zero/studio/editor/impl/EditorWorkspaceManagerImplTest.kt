package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EditorWorkspaceManagerImplTest {

    @Test
    fun `saveActive returns false when no active session`() = runTest {
        val manager = EditorWorkspaceManagerImpl(PersistentEditorWorkspace(PersistentEditorSessionFactory()))
        assertFalse(manager.saveActive())
    }

    @Test
    fun `openFile and saveActive persists content`() = runTest {
        val dir = Files.createTempDirectory("ws-manager-test")
        val file = dir.resolve("demo.txt")
        Files.writeString(file, "old")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val session = manager.openFile(file.toString(), "plaintext")
        session.execute(EditorCommand.SetText("new"))

        assertTrue(manager.saveActive())
        assertEquals("new", Files.readString(file))
    }

    @Test
    fun `saveAll saves every session`() = runTest {
        val dir = Files.createTempDirectory("ws-manager-test-all")
        val f1 = dir.resolve("a.txt")
        val f2 = dir.resolve("b.txt")
        Files.writeString(f1, "a")
        Files.writeString(f2, "b")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val s1 = manager.openFile(f1.toString(), "plaintext")
        val s2 = manager.openFile(f2.toString(), "plaintext")
        s1.execute(EditorCommand.SetText("A1"))
        s2.execute(EditorCommand.SetText("B1"))

        val count = manager.saveAll()

        assertEquals(2, count)
        assertEquals("A1", Files.readString(f1))
        assertEquals("B1", Files.readString(f2))
    }
}
