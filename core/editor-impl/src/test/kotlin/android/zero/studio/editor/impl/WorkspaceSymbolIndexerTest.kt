package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkspaceSymbolIndexerTest {

    @Test
    fun `rebuild index should collect function and class symbols`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        workspace.newSession(
            filePath = "/a.kt",
            initialText = """
                class User {
                }
                fun loadData() {}
            """.trimIndent(),
        )

        val indexer = WorkspaceSymbolIndexer(workspace)
        val count = indexer.rebuildIndex()

        assertEquals(2, count)
        assertTrue(indexer.symbols().any { it.name == "User" })
        assertTrue(indexer.symbols().any { it.name == "loadData" })
    }

    @Test
    fun `findByPrefix should filter symbols`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        workspace.newSession(filePath = "/a.kt", initialText = "fun loadA() {}\nfun listB() {}")

        val indexer = WorkspaceSymbolIndexer(workspace)
        indexer.rebuildIndex()

        val filtered = indexer.findByPrefix("loa")
        assertEquals(1, filtered.size)
        assertEquals("loadA", filtered.first().name)
    }
}
