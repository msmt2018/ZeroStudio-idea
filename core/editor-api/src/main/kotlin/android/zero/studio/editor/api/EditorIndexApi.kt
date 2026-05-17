package android.zero.studio.editor.api

data class IndexedSymbol(
    val name: String,
    val sessionId: SessionId,
    val filePath: String?,
    val line: Int,
)

interface IndexWorkspaceSymbolsUseCase {
    suspend fun rebuildIndex(): Int
    fun symbols(): List<IndexedSymbol>
    fun findByPrefix(prefix: String): List<IndexedSymbol>
}
