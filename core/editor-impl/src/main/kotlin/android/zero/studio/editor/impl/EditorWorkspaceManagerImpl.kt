package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.EditorWorkspaceManager

class EditorWorkspaceManagerImpl(
    private val workspace: EditorWorkspace,
) : EditorWorkspaceManager {
    override suspend fun openFile(path: String, languageId: String): EditorSession {
        return workspace.newSession(filePath = path, initialText = "", languageId = languageId)
    }

    override suspend fun newScratch(initialText: String, languageId: String): EditorSession {
        return workspace.newSession(filePath = null, initialText = initialText, languageId = languageId)
    }

    override suspend fun saveActive(): Boolean {
        val active = workspace.activeSessionId.value ?: return false
        val session = workspace.sessions.value.firstOrNull { it.id == active } ?: return false
        session.save()
        return true
    }

    override suspend fun saveAll(): Int {
        workspace.sessions.value.forEach { it.save() }
        return workspace.sessions.value.size
    }
}
