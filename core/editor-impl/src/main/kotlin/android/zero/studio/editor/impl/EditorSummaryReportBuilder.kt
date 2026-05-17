package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorMetricsUseCase
import android.zero.studio.editor.api.ExportEditorStateUseCase

class EditorSummaryReportBuilder(
    private val exportUseCase: ExportEditorStateUseCase,
    private val metricsUseCase: EditorMetricsUseCase,
) {
    suspend fun buildMarkdownReport(): String {
        val sessions = exportUseCase.exportOpenSessions()
        val metrics = metricsUseCase.metrics()

        val header = "# Editor Runtime Summary\n"
        val sessionLines = buildString {
            append("\n## Sessions (${sessions.size})\n")
            sessions.forEachIndexed { idx, s ->
                val path = s.filePath ?: "<scratch>"
                append("- ${idx + 1}. `${path}` dirty=${s.isDirty} chars=${s.text.length}\n")
            }
        }

        val metricLines = buildString {
            append("\n## Metrics (${metrics.size})\n")
            metrics.forEach {
                append("- `${it.name}` = ${it.count}\n")
            }
        }

        return header + sessionLines + metricLines
    }
}
