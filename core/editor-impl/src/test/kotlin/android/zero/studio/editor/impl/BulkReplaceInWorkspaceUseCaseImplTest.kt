package android.zero.studio.editor.impl

import android.zero.studio.editor.api.BulkReplaceRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BulkReplaceInWorkspaceUseCaseImplTest {

    @Test
    fun `bulk replace should update matching sessions and counts`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        workspace.newSession(filePath = "/a.txt", initialText = "hello world hello")
        workspace.newSession(filePath = "/b.txt", initialText = "no match")

        val useCase = BulkReplaceInWorkspaceUseCaseImpl(workspace)
        val result = useCase(BulkReplaceRequest(query = "hello", replacement = "hi"))

        assertEquals(1, result.touchedSessions)
        assertEquals(2, result.replacedMatches)
        assertEquals("hi world hi", workspace.sessions.value.first { it.filePath == "/a.txt" }.state.value.text)
    }

    @Test
    fun `bulk replace should ignore blank query`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        workspace.newSession(filePath = "/a.txt", initialText = "abc")

        val useCase = BulkReplaceInWorkspaceUseCaseImpl(workspace)
        val result = useCase(BulkReplaceRequest(query = "   ", replacement = "x"))

        assertEquals(0, result.touchedSessions)
        assertEquals(0, result.replacedMatches)
    }
}
