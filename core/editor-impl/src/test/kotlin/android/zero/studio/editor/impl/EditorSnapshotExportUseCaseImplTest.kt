package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorSnapshotExportUseCaseImplTest {
    @Test
    fun `export should include all open sessions with dirty state`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val s1 = workspace.newSession(filePath = "/a.txt", initialText = "a")
        workspace.newSession(filePath = null, initialText = "scratch")
        s1.execute(EditorCommand.SetText("a2"))

        val useCase = EditorSnapshotExportUseCaseImpl(workspace)
        val out = useCase.exportOpenSessions()

        assertEquals(2, out.size)
        assertTrue(out.first { it.filePath == "/a.txt" }.isDirty)
    }
}
