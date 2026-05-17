package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSnapshot
import android.zero.studio.editor.api.EditorViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Sora 适配桥（生命周期+双向同步骨架）。
 *
 * 当前仍不直接依赖 Sora；通过接口留出注入点，便于后续接入真实 CodeEditor。
 */
class SoraEditorBridge(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val renderer: EditorRenderer = NoopEditorRenderer,
) : EditorViewAdapter {

    private var boundSession: EditorSession? = null
    private var collectJob: Job? = null

    override fun bind(session: EditorSession) {
        unbind()
        boundSession = session
        renderer.render(session.state.value)
        collectJob = scope.launch {
            session.state.collectLatest { snapshot ->
                renderer.render(snapshot)
            }
        }
    }

    override fun unbind() {
        collectJob?.cancel()
        collectJob = null
        boundSession = null
    }

    override fun applySnapshot(snapshot: EditorSnapshot) {
        renderer.render(snapshot)
    }

    suspend fun onTextChanged(newText: String) {
        boundSession?.execute(EditorCommand.SetText(newText))
    }

    suspend fun onCursorMoved(line: Int, column: Int) {
        boundSession?.goTo(line, column)
    }

    suspend fun onSaveRequested() {
        boundSession?.save()
    }
}

fun interface EditorRenderer {
    fun render(snapshot: EditorSnapshot)
}

object NoopEditorRenderer : EditorRenderer {
    override fun render(snapshot: EditorSnapshot) = Unit
}
