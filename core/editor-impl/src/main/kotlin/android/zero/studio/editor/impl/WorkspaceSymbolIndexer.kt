package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorWorkspace
import android.zero.studio.editor.api.IndexWorkspaceSymbolsUseCase
import android.zero.studio.editor.api.IndexedSymbol

class WorkspaceSymbolIndexer(
    private val workspace: EditorWorkspace,
) : IndexWorkspaceSymbolsUseCase {
    private var cache: List<IndexedSymbol> = emptyList()

    override suspend fun rebuildIndex(): Int {
        val out = mutableListOf<IndexedSymbol>()
        workspace.sessions.value.forEach { session ->
            val lines = session.state.value.text.lines()
            lines.forEachIndexed { idx, line ->
                parseSymbol(line)?.let { symbol ->
                    out += IndexedSymbol(
                        name = symbol,
                        sessionId = session.id,
                        filePath = session.filePath,
                        line = idx + 1,
                    )
                }
            }
        }
        cache = out
        return cache.size
    }

    override fun symbols(): List<IndexedSymbol> = cache

    override fun findByPrefix(prefix: String): List<IndexedSymbol> {
        if (prefix.isBlank()) return emptyList()
        return cache.filter { it.name.startsWith(prefix) }
    }

    private fun parseSymbol(line: String): String? {
        val trimmed = line.trim()
        return when {
            trimmed.startsWith("fun ") -> trimmed.removePrefix("fun ").takeWhile { it != '(' && !it.isWhitespace() }
            trimmed.startsWith("class ") -> trimmed.removePrefix("class ").takeWhile { !it.isWhitespace() && it != '{' }
            else -> null
        }.ifBlank { null }
    }
}
