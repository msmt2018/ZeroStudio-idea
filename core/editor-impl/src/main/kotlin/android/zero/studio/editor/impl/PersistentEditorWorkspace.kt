package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSessionFactory
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.SessionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 面向“文件打开”场景的 workspace 实现。
 */
class PersistentEditorWorkspace(
    private val sessionFactory: EditorSessionFactory,
) : EditorWorkspace {
    private val _sessions = MutableStateFlow<List<EditorSession>>(emptyList())
    override val sessions: StateFlow<List<EditorSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<SessionId?>(null)
    override val activeSessionId: StateFlow<SessionId?> = _activeSessionId.asStateFlow()

    override suspend fun newSession(filePath: String?, initialText: String, languageId: String): EditorSession {
        val session = if (filePath.isNullOrBlank()) {
            InMemoryEditorEngine().open(filePath = null, initialText = initialText, languageId = languageId)
        } else {
            sessionFactory.openFromPath(filePath, languageId)
        }
        _sessions.value = _sessions.value + session
        _activeSessionId.value = session.id
        return session
    }

    override suspend fun activate(sessionId: SessionId) {
        if (_sessions.value.any { it.id == sessionId }) {
            _activeSessionId.value = sessionId
        }
    }

    override suspend fun close(sessionId: SessionId) {
        val session = _sessions.value.find { it.id == sessionId } ?: return
        session.close()
        _sessions.value = _sessions.value.filterNot { it.id == sessionId }
        if (_activeSessionId.value == sessionId) {
            _activeSessionId.value = _sessions.value.lastOrNull()?.id
        }
    }
}
