package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorDocumentStore
import android.zero.studio.editor.api.EditorEngine
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSessionFactory

class PersistentEditorSessionFactory(
    private val engine: EditorEngine = InMemoryEditorEngine(),
    private val store: EditorDocumentStore = FileEditorDocumentStore(),
) : EditorSessionFactory {
    override suspend fun openFromPath(path: String, languageId: String): EditorSession {
        val text = store.load(path)
        return engine.open(filePath = path, initialText = text, languageId = languageId)
    }
}
