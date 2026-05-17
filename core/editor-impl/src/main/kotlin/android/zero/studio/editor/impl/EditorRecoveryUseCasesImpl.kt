package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CaptureRecoveryUseCase
import android.zero.studio.editor.api.EditorRecoveryStore
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.RecoverySnapshot
import android.zero.studio.editor.api.RestoreRecoveryUseCase

class CaptureRecoveryUseCaseImpl(
    private val workspace: EditorWorkspace,
    private val recoveryStore: EditorRecoveryStore,
) : CaptureRecoveryUseCase {
    override suspend fun captureAllOpenSessions(): Int {
        workspace.sessions.value.forEach { session ->
            recoveryStore.save(
                RecoverySnapshot(
                    sessionId = session.id,
                    filePath = session.filePath,
                    text = session.state.value.text,
                ),
            )
        }
        return workspace.sessions.value.size
    }
}

class RestoreRecoveryUseCaseImpl(
    private val workspace: EditorWorkspace,
    private val recoveryStore: EditorRecoveryStore,
) : RestoreRecoveryUseCase {
    override suspend fun restoreAll(): Int {
        val items = recoveryStore.snapshots.value
        items.forEach {
            workspace.newSession(filePath = it.filePath, initialText = it.text)
        }
        return items.size
    }
}
