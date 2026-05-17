package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorCoordinatorImplTest {

    @Test
    fun `open should update uiState`() = runTest {
        val dir = Files.createTempDirectory("coord-test-open")
        val file = dir.resolve("demo.txt")
        Files.writeString(file, "hello")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val open = OpenEditorUseCaseImpl(manager)
        val save = SaveEditorUseCaseImpl(manager)
        val coordinator = EditorCoordinatorImpl(workspace, open, save)

        coordinator.open(file.toString())

        assertEquals(1, coordinator.uiState.value.openSessionCount)
        assertEquals(file.toString(), coordinator.uiState.value.activeFilePath)
    }

    @Test
    fun `saveActive should return true and refresh dirty state`() = runTest {
        val dir = Files.createTempDirectory("coord-test-save")
        val file = dir.resolve("demo.txt")
        Files.writeString(file, "old")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val open = OpenEditorUseCaseImpl(manager)
        val save = SaveEditorUseCaseImpl(manager)
        val coordinator = EditorCoordinatorImpl(workspace, open, save)

        coordinator.open(file.toString())
        val session = workspace.sessions.value.first()
        session.execute(EditorCommand.SetText("new"))

        assertTrue(coordinator.saveActive())
        assertEquals("new", Files.readString(file))
        assertTrue(!coordinator.uiState.value.isDirty)
    }
}
