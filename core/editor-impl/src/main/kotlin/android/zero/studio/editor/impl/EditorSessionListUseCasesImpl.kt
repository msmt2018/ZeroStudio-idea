package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CloseEditorSessionUseCase
import android.zero.studio.editor.api.EditorSessionItem
import android.zero.studio.editor.api.ObserveEditorSessionsUseCase
import android.zero.studio.editor.api.RecentFilesUseCase
import android.zero.studio.editor.api.EditorRecentFilesStore
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ObserveEditorSessionsUseCaseImpl(
    private val workspace: EditorWorkspace,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : ObserveEditorSessionsUseCase {
    private val _sessions = MutableStateFlow<List<EditorSessionItem>>(emptyList())
    override val sessions: StateFlow<List<EditorSessionItem>> = _sessions.asStateFlow()

    init {
        scope.launch {
            workspace.sessions.collectLatest { list ->
                val active = workspace.activeSessionId.value
                _sessions.value = list.map {
                    EditorSessionItem(
                        id = it.id,
                        filePath = it.filePath,
                        isDirty = it.state.value.isDirty,
                        isActive = it.id == active,
                    )
                }
            }
        }
    }
}

class CloseEditorSessionUseCaseImpl(
    private val workspace: EditorWorkspace,
) : CloseEditorSessionUseCase {
    override suspend fun close(sessionId: SessionId) {
        workspace.close(sessionId)
    }
}

class RecentFilesUseCaseImpl(
    private val store: EditorRecentFilesStore,
) : RecentFilesUseCase {
    override val recentFiles: StateFlow<List<String>> = store.recentFiles

    override suspend fun clear() {
        store.clear()
    }
}
