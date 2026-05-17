package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSnapshot
import android.zero.studio.editor.api.EditorStats
import android.zero.studio.editor.api.SearchMatch
import android.zero.studio.editor.api.SearchOptions
import android.zero.studio.editor.api.SessionId
import kotlinx.coroutines.flow.StateFlow

/**
 * 为持久化场景增加“保存到文件系统”能力。
 */
class PersistentEditorSession(
    private val delegate: EditorSession,
    private val store: FileEditorDocumentStore,
) : EditorSession {
    override val id: SessionId = delegate.id
    override val filePath: String? = delegate.filePath
    override val state: StateFlow<EditorSnapshot> = delegate.state

    override suspend fun execute(command: EditorCommand) {
        delegate.execute(command)
    }

    override suspend fun goTo(line: Int, column: Int) {
        delegate.goTo(line, column)
    }

    override suspend fun find(query: String, options: SearchOptions): List<SearchMatch> {
        return delegate.find(query, options)
    }

    override suspend fun stats(): EditorStats {
        return delegate.stats()
    }

    override suspend fun save() {
        val path = filePath
        if (!path.isNullOrBlank()) {
            val normalized = DocumentTextNormalizer.normalizeLineEnding(
                state.value.text,
                state.value.documentMeta.lineEnding,
            )
            store.save(path, normalized, state.value.documentMeta.charset)
        }
        delegate.save()
    }

    override suspend fun close() {
        delegate.close()
    }
}
