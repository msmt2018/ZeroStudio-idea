package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class PersistentEditorSessionFactoryTest {

    @Test
    fun `save should persist session text to file`() = runTest {
        val dir = Files.createTempDirectory("persist-session-test")
        val file = dir.resolve("main.kt")
        Files.writeString(file, "old")

        val factory = PersistentEditorSessionFactory()
        val session = factory.openFromPath(file.toString(), "kotlin")

        session.execute(EditorCommand.SetText("new-content"))
        session.save()

        assertEquals("new-content", Files.readString(file))
    }

    @Test
    fun `save should respect session line ending strategy`() = runTest {
        val dir = Files.createTempDirectory("persist-session-test-le")
        val file = dir.resolve("main.txt")
        Files.writeString(file, "a\nb\n")

        val factory = PersistentEditorSessionFactory()
        val session = factory.openFromPath(file.toString(), "plaintext")
        session.execute(EditorCommand.SetText("x\ny\n"))
        session.save()

        // default line ending is LF in current engine metadata
        assertEquals("x\ny\n", Files.readString(file))
    }
}
