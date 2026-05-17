package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCommand
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateEditorSessionsUseCaseImplTest {

    @Test
    fun `validate should report tab usage`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val s = workspace.newSession(filePath = "/a.kt", initialText = "fun\tmain")
        s.execute(EditorCommand.SetText("fun\tmain"))

        val useCase = ValidateEditorSessionsUseCaseImpl(workspace)
        val issues = useCase.validateOpenSessions()

        assertEquals(1, issues.size)
        assertTrue(issues.first().message.contains("TAB"))
    }

    @Test
    fun `validate should report large docs`() = runTest {
        val workspace = InMemoryEditorWorkspace()
        val text = "x".repeat(100_001)
        workspace.newSession(filePath = "/big.txt", initialText = text)

        val useCase = ValidateEditorSessionsUseCaseImpl(workspace)
        val issues = useCase.validateOpenSessions()

        assertTrue(issues.any { it.message.contains("Large document") })
    }
}
