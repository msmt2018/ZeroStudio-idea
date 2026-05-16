package android.zero.studio.editor.api

import kotlinx.coroutines.flow.StateFlow

data class RecoverySnapshot(
    val sessionId: SessionId,
    val filePath: String?,
    val text: String,
)

interface EditorRecoveryStore {
    val snapshots: StateFlow<List<RecoverySnapshot>>
    suspend fun save(snapshot: RecoverySnapshot)
    suspend fun remove(sessionId: SessionId)
    suspend fun clear()
}

interface CaptureRecoveryUseCase {
    suspend fun captureAllOpenSessions(): Int
}

interface RestoreRecoveryUseCase {
    suspend fun restoreAll(): Int
}
