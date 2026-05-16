package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EditorSummaryReportBuilderTest {
    @Test
    fun `report should contain session and metrics sections`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val s = workspace.newSession(filePath = "/demo.txt", initialText = "x")
        s.execute(EditorCommand.SetText("xyz"))

        val export = EditorSnapshotExportUseCaseImpl(workspace)
        val collector = InMemoryEditorMetricsCollector().apply { record("open_many_invoked") }
        val metrics = MetricsEditorMetricsUseCase(collector)
        val report = EditorSummaryReportBuilder(export, metrics).buildMarkdownReport()

        assertTrue(report.contains("# Editor Runtime Summary"))
        assertTrue(report.contains("## Sessions"))
        assertTrue(report.contains("## Metrics"))
        assertTrue(report.contains("open_many_invoked"))
    }
}
