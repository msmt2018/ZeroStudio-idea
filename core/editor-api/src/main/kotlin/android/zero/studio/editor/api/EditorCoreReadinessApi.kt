package android.zero.studio.editor.api

data class ReadinessItem(
    val key: String,
    val done: Boolean,
)

data class EditorCoreReadinessReport(
    val items: List<ReadinessItem>,
) {
    val isReady: Boolean get() = items.all { it.done }
    val completionRate: Double get() = if (items.isEmpty()) 0.0 else items.count { it.done }.toDouble() / items.size
}

interface EditorCoreReadinessUseCase {
    fun report(): EditorCoreReadinessReport
}
