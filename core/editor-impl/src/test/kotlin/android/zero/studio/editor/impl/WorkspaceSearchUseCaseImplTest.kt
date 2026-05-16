package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import android.zero.studio.editor.api.SearchInWorkspaceRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkspaceSearchUseCaseImplTest {

    @Test
    fun `search should return matching sessions only`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val s1 = workspace.newSession(filePath = "/a.kt", initialText = "fun main")
        workspace.newSession(filePath = "/b.kt", initialText = "class Box")
        s1.execute(EditorCommand.SetText("fun main\nprintln(\"x\")"))

        val useCase = WorkspaceSearchUseCaseImpl(workspace)
        val result = useCase(SearchInWorkspaceRequest("fun"))

        assertEquals(1, result.size)
        assertEquals("/a.kt", result.first().filePath)
        assertTrue(result.first().matches.isNotEmpty())
    }

    @Test
    fun `blank query should return empty list`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        workspace.newSession(filePath = "/a.kt", initialText = "abc")

        val useCase = WorkspaceSearchUseCaseImpl(workspace)
        assertTrue(useCase(SearchInWorkspaceRequest("   ")).isEmpty())
    }
}
