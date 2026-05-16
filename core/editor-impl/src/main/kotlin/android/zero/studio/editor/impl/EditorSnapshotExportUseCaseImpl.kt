package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.ExportEditorStateUseCase
import android.zero.studio.editor.api.ExportSessionSnapshot

class EditorSnapshotExportUseCaseImpl(
    private val workspace: EditorWorkspace,
) : ExportEditorStateUseCase {
    override suspend fun exportOpenSessions(): List<ExportSessionSnapshot> {
        return workspace.sessions.value.map {
            ExportSessionSnapshot(
                filePath = it.filePath,
                text = it.state.value.text,
                isDirty = it.state.value.isDirty,
            )
        }
    }
}
