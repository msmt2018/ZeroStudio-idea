package android.zero.studio.editor.impl

import android.zero.studio.editor.api.OpenEditorRequest
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class TrackingOpenEditorUseCaseTest {

    @Test
    fun `open should record recent file`() = runTest {
        val dir = Files.createTempDirectory("tracking-open-test")
        val file = dir.resolve("x.txt")
        Files.writeString(file, "1")

        val manager = EditorWorkspaceManagerImpl(PersistentEditorWorkspace(PersistentEditorSessionFactory()))
        val base = OpenEditorUseCaseImpl(manager)
        val recent = InMemoryRecentFilesStore()
        val tracking = TrackingOpenEditorUseCase(base, recent)

        tracking(OpenEditorRequest(file.toString(), "plaintext"))

        assertEquals(listOf(file.toString()), recent.recentFiles.value)
    }
}
