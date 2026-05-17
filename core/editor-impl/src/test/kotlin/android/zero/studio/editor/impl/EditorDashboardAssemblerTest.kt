package android.zero.studio.editor.impl

import android.zero.studio.editor.api.OpenEditorRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class EditorDashboardAssemblerTest {

    @Test
    fun `snapshot should compose coordinator sessions and recents`() = runTest {
        val dir = Files.createTempDirectory("dashboard-test")
        val file = dir.resolve("demo.txt")
        Files.writeString(file, "hello")

        val workspace = PersistentEditorWorkspace(PersistentEditorSessionFactory())
        val manager = EditorWorkspaceManagerImpl(workspace)
        val openBase = OpenEditorUseCaseImpl(manager)
        val recentStore = InMemoryRecentFilesStore()
        val open = TrackingOpenEditorUseCase(openBase, recentStore)
        val save = SaveEditorUseCaseImpl(manager)
        val coordinator = EditorCoordinatorImpl(workspace, open, save)
        val sessionsUC = ObserveEditorSessionsUseCaseImpl(
            workspace,
            scope = CoroutineScope(Job() + Dispatchers.Unconfined),
        )
        val recentUC = RecentFilesUseCaseImpl(recentStore)
        val dashboard = EditorDashboardAssembler(coordinator, sessionsUC, recentUC)

        open(OpenEditorRequest(file.toString()))
        coordinator.open(file.toString())

        val snap = dashboard.snapshot()
        assertEquals(1, snap.openSessionCount)
        assertEquals(file.toString(), snap.activeFilePath)
        assertEquals(listOf(file.toString()), snap.recentFiles)
        assertEquals(listOf(file.toString()), snap.sessionTitles)
    }
}
