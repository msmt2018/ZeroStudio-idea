package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorRecoveryStore
import android.zero.studio.editor.api.RecoverySnapshot
import android.zero.studio.editor.api.SessionId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryEditorRecoveryStore : EditorRecoveryStore {
    private val _snapshots = MutableStateFlow<List<RecoverySnapshot>>(emptyList())
    override val snapshots: StateFlow<List<RecoverySnapshot>> = _snapshots.asStateFlow()

    override suspend fun save(snapshot: RecoverySnapshot) {
        _snapshots.value = listOf(snapshot) + _snapshots.value.filterNot { it.sessionId == snapshot.sessionId }
    }

    override suspend fun remove(sessionId: SessionId) {
        _snapshots.value = _snapshots.value.filterNot { it.sessionId == sessionId }
    }

    override suspend fun clear() {
        _snapshots.value = emptyList()
    }
}
