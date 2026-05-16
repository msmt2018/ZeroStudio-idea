package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.OpenEditorRequest
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorUseCasesImplTest {

    @Test
    fun `open usecase should open file into session`() = runTest {
        val dir = Files.createTempDirectory("open-usecase-test")
        val file = dir.resolve("note.txt")
        Files.writeString(file, "hello")

        val manager = EditorWorkspaceManagerImpl(PersistentEditorWorkspace(PersistentEditorSessionFactory()))
        val useCase = OpenEditorUseCaseImpl(manager)

        val session = useCase(OpenEditorRequest(path = file.toString(), languageId = "plaintext"))
        assertEquals("hello", session.state.value.text)
    }

    @Test
    fun `save usecase should persist active and all`() = runTest {
        val dir = Files.createTempDirectory("save-usecase-test")
        val f1 = dir.resolve("a.txt")
        val f2 = dir.resolve("b.txt")
        Files.writeString(f1, "a")
        Files.writeString(f2, "b")

        val manager = EditorWorkspaceManagerImpl(PersistentEditorWorkspace(PersistentEditorSessionFactory()))
        val open = OpenEditorUseCaseImpl(manager)
        val save = SaveEditorUseCaseImpl(manager)

        val s1 = open(OpenEditorRequest(f1.toString()))
        val s2 = open(OpenEditorRequest(f2.toString()))
        s1.execute(EditorCommand.SetText("A2"))
        s2.execute(EditorCommand.SetText("B2"))

        assertTrue(save.saveActive())
        val count = save.saveAll()

        assertEquals(2, count)
        assertEquals("A2", Files.readString(f1))
        assertEquals("B2", Files.readString(f2))
    }
}
