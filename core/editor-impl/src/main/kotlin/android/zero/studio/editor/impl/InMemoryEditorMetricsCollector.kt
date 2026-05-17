package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorMetricsCollector
import android.zero.studio.editor.api.EditorOperationMetric
import java.util.concurrent.ConcurrentHashMap

class InMemoryEditorMetricsCollector : EditorMetricsCollector {
    private val map = ConcurrentHashMap<String, Int>()

    override fun record(name: String) {
        map.compute(name) { _, old -> (old ?: 0) + 1 }
    }

    override fun snapshot(): List<EditorOperationMetric> {
        return map.entries
            .sortedBy { it.key }
            .map { EditorOperationMetric(name = it.key, count = it.value) }
    }

    override fun reset() {
        map.clear()
    }
}
