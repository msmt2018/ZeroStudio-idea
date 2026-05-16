package android.zero.studio.editor.impl

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

        session.execute(android.zero.studio.editor.api.EditorCommand.SetText("new-content"))
        session.save()

        assertEquals("new-content", Files.readString(file))
    }
}
