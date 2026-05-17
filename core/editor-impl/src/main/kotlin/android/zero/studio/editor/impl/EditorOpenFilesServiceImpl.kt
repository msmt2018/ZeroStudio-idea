package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorOpenFilesService
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.OpenManyRequest
import android.zero.studio.editor.api.OpenManyResult
import android.zero.studio.editor.api.OpenEditorRequest
import android.zero.studio.editor.api.OpenEditorUseCase
import android.zero.studio.editor.api.OpenManyEditorsUseCase

class EditorOpenFilesServiceImpl(
    private val openEditorUseCase: OpenEditorUseCase,
    private val openManyEditorsUseCase: OpenManyEditorsUseCase,
) : EditorOpenFilesService {
    override suspend fun open(path: String, languageId: String): EditorSession {
        return openEditorUseCase(OpenEditorRequest(path, languageId))
    }

    override suspend fun openMany(paths: List<String>, languageId: String): OpenManyResult {
        return openManyEditorsUseCase(OpenManyRequest(paths = paths, languageId = languageId))
    }
}
