package android.zero.studio.editor.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryEditorMetricsCollectorTest {
    @Test
    fun `record snapshot reset should work`() {
        val collector = InMemoryEditorMetricsCollector()
        collector.record("a")
        collector.record("a")
        collector.record("b")

        val snap = collector.snapshot()
        assertEquals(2, snap.first { it.name == "a" }.count)
        assertEquals(1, snap.first { it.name == "b" }.count)

        collector.reset()
        assertTrue(collector.snapshot().isEmpty())
    }
}
