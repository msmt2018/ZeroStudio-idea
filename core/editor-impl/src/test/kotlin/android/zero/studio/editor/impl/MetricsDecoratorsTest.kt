package android.zero.studio.editor.impl

import android.zero.studio.editor.api.OpenManyRequest
import android.zero.studio.editor.api.OpenManyResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetricsDecoratorsTest {

    @Test
    fun `metrics wrappers should record batch operations`() = runTest {
        val collector = InMemoryEditorMetricsCollector()
        val open = MetricsOpenManyEditorsUseCase(
            delegate = { OpenManyResult(2, emptyList()) },
            collector = collector,
        )
        val save = MetricsSaveDirtyEditorsUseCase(delegate = { 1 }, collector = collector)
        val close = MetricsCloseAllEditorsUseCase(delegate = { 2 }, collector = collector)

        open(OpenManyRequest(listOf("/a", "/b")))
        save()
        close()

        val metrics = collector.snapshot().associate { it.name to it.count }
        assertEquals(1, metrics["open_many_invoked"])
        assertEquals(1, metrics["save_dirty_invoked"])
        assertEquals(1, metrics["close_all_invoked"])
    }

    @Test
    fun `metrics usecase should expose and reset`() {
        val collector = InMemoryEditorMetricsCollector()
        collector.record("x")
        val useCase = MetricsEditorMetricsUseCase(collector)

        assertEquals(1, useCase.metrics().first().count)
        useCase.reset()
        assertTrue(useCase.metrics().isEmpty())
    }
}
