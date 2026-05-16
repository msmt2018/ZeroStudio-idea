package android.zero.studio.editor.api

data class EditorOperationMetric(
    val name: String,
    val count: Int,
)

interface EditorMetricsCollector {
    fun record(name: String)
    fun snapshot(): List<EditorOperationMetric>
    fun reset()
}

interface EditorMetricsUseCase {
    fun metrics(): List<EditorOperationMetric>
    fun reset()
}
