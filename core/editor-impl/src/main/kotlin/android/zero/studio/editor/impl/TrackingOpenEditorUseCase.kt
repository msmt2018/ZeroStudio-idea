package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorRecentFilesStore
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.OpenEditorRequest
import android.zero.studio.editor.api.OpenEditorUseCase

class TrackingOpenEditorUseCase(
    private val delegate: OpenEditorUseCase,
    private val recentFilesStore: EditorRecentFilesStore,
) : OpenEditorUseCase {
    override suspend fun invoke(request: OpenEditorRequest): EditorSession {
        val session = delegate(request)
        recentFilesStore.recordOpen(request.path)
        return session
    }
}
