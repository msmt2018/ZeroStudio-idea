package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorAutosaveController
import android.zero.studio.editor.api.EditorWorkspace

class EditorAutosaveControllerImpl(
    private val workspace: EditorWorkspace,
) : EditorAutosaveController {
    override suspend fun flushDirtySessions(): Int {
        var saved = 0
        workspace.sessions.value.forEach { session ->
            if (session.state.value.isDirty && !session.filePath.isNullOrBlank()) {
                session.save()
                saved += 1
            }
        }
        return saved
    }
}
