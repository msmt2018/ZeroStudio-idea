package android.zero.studio.editor.impl

import android.zero.studio.editor.api.SearchInWorkspaceRequest
import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EditorCoreFacadeFactoryTest {
    @Test
    fun `factory should build usable facade`() = runTest {
        val facade = EditorCoreFacadeFactory.createDefault()
        assertNotNull(facade.coordinator)
        assertNotNull(facade.workspace)

        val dir = Files.createTempDirectory("facade-test")
        val file = dir.resolve("demo.kt")
        Files.writeString(file, "fun hello() {}")

        facade.openFilesService.open(file.toString(), "kotlin")
        facade.coordinator.open(file.toString(), "kotlin")

        val search = facade.searchInWorkspaceUseCase(SearchInWorkspaceRequest("hello"))
        assertTrue(search.isNotEmpty())

        val export = facade.exportEditorStateUseCase.exportOpenSessions()
        assertTrue(export.isNotEmpty())
        assertEquals(file.toString(), export.first().filePath)
    }
}
