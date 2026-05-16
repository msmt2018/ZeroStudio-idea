package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class EditorAutosaveControllerImplTest {

    @Test
    fun `flushDirtySessions saves only dirty file-backed sessions`() = runTest {
        val dir = Files.createTempDirectory("autosave-test")
        val f1 = dir.resolve("a.txt")
        val f2 = dir.resolve("b.txt")
        Files.writeString(f1, "A")
        Files.writeString(f2, "B")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val s1 = workspace.newSession(filePath = f1.toString(), languageId = "plaintext")
        val s2 = workspace.newSession(filePath = f2.toString(), languageId = "plaintext")
        workspace.newSession(filePath = null, initialText = "scratch")

        s1.execute(EditorCommand.SetText("A2"))
        // s2 remains clean

        val autosave = EditorAutosaveControllerImpl(workspace)
        val savedCount = autosave.flushDirtySessions()

        assertEquals(1, savedCount)
        assertEquals("A2", Files.readString(f1))
        assertEquals("B", Files.readString(f2))
    }
}
