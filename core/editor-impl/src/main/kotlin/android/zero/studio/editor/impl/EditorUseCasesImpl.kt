package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorWorkspaceManager
import android.zero.studio.editor.api.OpenEditorRequest
import android.zero.studio.editor.api.OpenEditorUseCase
import android.zero.studio.editor.api.SaveEditorUseCase

class OpenEditorUseCaseImpl(
    private val workspaceManager: EditorWorkspaceManager,
) : OpenEditorUseCase {
    override suspend fun invoke(request: OpenEditorRequest): EditorSession {
        return workspaceManager.openFile(request.path, request.languageId)
    }
}

class SaveEditorUseCaseImpl(
    private val workspaceManager: EditorWorkspaceManager,
) : SaveEditorUseCase {
    override suspend fun saveActive(): Boolean = workspaceManager.saveActive()

    override suspend fun saveAll(): Int = workspaceManager.saveAll()
}
