package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CloseAllEditorsUseCase
import android.zero.studio.editor.api.EditorAutosaveController
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.OpenManyEditorsUseCase
import android.zero.studio.editor.api.OpenManyRequest
import android.zero.studio.editor.api.OpenManyResult
import android.zero.studio.editor.api.OpenEditorRequest
import android.zero.studio.editor.api.OpenEditorUseCase
import android.zero.studio.editor.api.SaveDirtyEditorsUseCase

class OpenManyEditorsUseCaseImpl(
    private val openEditorUseCase: OpenEditorUseCase,
) : OpenManyEditorsUseCase {
    override suspend fun invoke(request: OpenManyRequest): OpenManyResult {
        val failed = mutableListOf<String>()
        var opened = 0

        request.paths.distinct().forEach { path ->
            runCatching {
                openEditorUseCase(OpenEditorRequest(path = path, languageId = request.languageId))
            }.onSuccess {
                opened += 1
            }.onFailure {
                failed += path
            }
        }

        return OpenManyResult(openedCount = opened, failedPaths = failed)
    }
}

class SaveDirtyEditorsUseCaseImpl(
    private val autosaveController: EditorAutosaveController,
) : SaveDirtyEditorsUseCase {
    override suspend fun invoke(): Int = autosaveController.flushDirtySessions()
}

class CloseAllEditorsUseCaseImpl(
    private val workspace: EditorWorkspace,
) : CloseAllEditorsUseCase {
    override suspend fun invoke(): Int {
        val all = workspace.sessions.value.map { it.id }
        all.forEach { workspace.close(it) }
        return all.size
    }
}
