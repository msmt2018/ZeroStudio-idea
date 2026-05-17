package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorAutosaveController
import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.OpenManyRequest
import android.zero.studio.editor.api.OpenEditorRequest
import android.zero.studio.editor.api.OpenEditorUseCase
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorBatchUseCasesImplTest {

    @Test
    fun `open many should deduplicate paths and collect failures`() = runTest {
        val dir = Files.createTempDirectory("batch-open-test")
        val ok = dir.resolve("ok.txt")
        Files.writeString(ok, "1")
        val bad = dir.resolve("missing.txt")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val baseOpen = OpenEditorUseCaseImpl(manager)
        val useCase = OpenManyEditorsUseCaseImpl(baseOpen)

        val result = useCase(OpenManyRequest(paths = listOf(ok.toString(), ok.toString(), bad.toString())))

        assertEquals(1, result.openedCount)
        assertEquals(listOf(bad.toString()), result.failedPaths)
    }

    @Test
    fun `save dirty usecase delegates to autosave controller`() = runTest {
        val useCase = SaveDirtyEditorsUseCaseImpl(object : EditorAutosaveController {
            override suspend fun flushDirtySessions(): Int = 3
        })
        assertEquals(3, useCase())
    }

    @Test
    fun `close all should close every session`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val open = OpenEditorUseCase {
            workspace.newSession(filePath = it.path, initialText = "x")
        }
        val openMany = OpenManyEditorsUseCaseImpl(open)
        openMany(OpenManyRequest(paths = listOf("/a", "/b")))

        val closeAll = CloseAllEditorsUseCaseImpl(workspace)
        val closed = closeAll()

        assertEquals(2, closed)
        assertTrue(workspace.sessions.value.isEmpty())
    }

    @Test
    fun `save dirty integrates with real autosave`() = runTest {
        val dir = Files.createTempDirectory("batch-save-test")
        val f = dir.resolve("a.txt")
        Files.writeString(f, "A")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val s = workspace.newSession(filePath = f.toString(), languageId = "plaintext")
        s.execute(EditorCommand.SetText("A2"))

        val saveDirty = SaveDirtyEditorsUseCaseImpl(EditorAutosaveControllerImpl(workspace))
        val count = saveDirty()

        assertEquals(1, count)
        assertEquals("A2", Files.readString(f))
    }
}
