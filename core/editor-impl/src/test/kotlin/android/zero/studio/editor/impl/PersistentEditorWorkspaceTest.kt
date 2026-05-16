package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PersistentEditorWorkspaceTest {

    @Test
    fun `newSession should load file content via session factory`() = runTest {
        val dir = Files.createTempDirectory("editor-ws-test")
        val file = dir.resolve("demo.kt")
        Files.writeString(file, "fun main() {}")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val session = workspace.newSession(filePath = file.toString(), languageId = "kotlin")

        assertEquals("fun main() {}", session.state.value.text)
        assertEquals(session.id, workspace.activeSessionId.value)
        assertEquals(1, workspace.sessions.value.size)
    }

    @Test
    fun `close should remove session and clear active when last session`() = runTest {
        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val session = workspace.newSession(filePath = null, initialText = "x")
        assertNotNull(workspace.activeSessionId.value)

        workspace.close(session.id)

        assertEquals(0, workspace.sessions.value.size)
        assertNull(workspace.activeSessionId.value)
    }
}
