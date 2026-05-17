package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorEngine
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSessionFactory

class PersistentEditorSessionFactory(
    private val engine: EditorEngine = InMemoryEditorEngine(),
    private val store: FileEditorDocumentStore = FileEditorDocumentStore(),
) : EditorSessionFactory {
    override suspend fun openFromPath(path: String, languageId: String): EditorSession {
        val text = store.load(path)
        val session = engine.open(filePath = path, initialText = text, languageId = languageId)
        return PersistentEditorSession(session, store)
    }
}
