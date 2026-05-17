package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class EditorOpenFilesServiceImplTest {
    @Test
    fun `open should delegate to OpenEditorUseCase`() = runTest {
        val dir = Files.createTempDirectory("open-service-test")
        val file = dir.resolve("a.txt")
        Files.writeString(file, "hello")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val open = OpenEditorUseCaseImpl(manager)
        val openMany = OpenManyEditorsUseCaseImpl(open)
        val service = EditorOpenFilesServiceImpl(open, openMany)

        val session = service.open(file.toString())
        assertEquals("hello", session.state.value.text)
    }

    @Test
    fun `openMany should report opened and failed`() = runTest {
        val dir = Files.createTempDirectory("open-many-service-test")
        val ok = dir.resolve("ok.txt")
        val bad = dir.resolve("missing.txt")
        Files.writeString(ok, "1")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val open = OpenEditorUseCaseImpl(manager)
        val openMany = OpenManyEditorsUseCaseImpl(open)
        val service = EditorOpenFilesServiceImpl(open, openMany)

        val result = service.openMany(listOf(ok.toString(), bad.toString()))
        assertEquals(1, result.openedCount)
        assertEquals(listOf(bad.toString()), result.failedPaths)
    }
}
