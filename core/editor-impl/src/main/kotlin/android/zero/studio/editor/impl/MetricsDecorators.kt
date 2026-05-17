package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CloseAllEditorsUseCase
import android.zero.studio.editor.api.EditorAutosaveController
import android.zero.studio.editor.api.EditorMetricsCollector
import android.zero.studio.editor.api.EditorMetricsUseCase
import android.zero.studio.editor.api.EditorOperationMetric
import android.zero.studio.editor.api.OpenManyEditorsUseCase
import android.zero.studio.editor.api.OpenManyRequest
import android.zero.studio.editor.api.OpenManyResult
import android.zero.studio.editor.api.SaveDirtyEditorsUseCase

class MetricsEditorMetricsUseCase(
    private val collector: EditorMetricsCollector,
) : EditorMetricsUseCase {
    override fun metrics(): List<EditorOperationMetric> = collector.snapshot()

    override fun reset() = collector.reset()
}

class MetricsOpenManyEditorsUseCase(
    private val delegate: OpenManyEditorsUseCase,
    private val collector: EditorMetricsCollector,
) : OpenManyEditorsUseCase {
    override suspend fun invoke(request: OpenManyRequest): OpenManyResult {
        collector.record("open_many_invoked")
        val result = delegate(request)
        collector.record("open_many_opened_${result.openedCount}")
        if (result.failedPaths.isNotEmpty()) collector.record("open_many_failed")
        return result
    }
}

class MetricsSaveDirtyEditorsUseCase(
    private val delegate: SaveDirtyEditorsUseCase,
    private val collector: EditorMetricsCollector,
) : SaveDirtyEditorsUseCase {
    override suspend fun invoke(): Int {
        collector.record("save_dirty_invoked")
        val count = delegate()
        collector.record("save_dirty_saved_$count")
        return count
    }
}

class MetricsCloseAllEditorsUseCase(
    private val delegate: CloseAllEditorsUseCase,
    private val collector: EditorMetricsCollector,
) : CloseAllEditorsUseCase {
    override suspend fun invoke(): Int {
        collector.record("close_all_invoked")
        val count = delegate()
        collector.record("close_all_closed_$count")
        return count
    }
}

class MetricsAutosaveController(
    private val delegate: EditorAutosaveController,
    private val collector: EditorMetricsCollector,
) : EditorAutosaveController {
    override suspend fun flushDirtySessions(): Int {
        collector.record("autosave_invoked")
        val count = delegate.flushDirtySessions()
        collector.record("autosave_saved_$count")
        return count
    }
}
