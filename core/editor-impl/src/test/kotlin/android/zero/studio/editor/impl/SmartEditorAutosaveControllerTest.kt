package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorAutosaveController
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SmartEditorAutosaveControllerTest {
    @Test
    fun `autosave disabled should skip flush`() = runTest {
        val prefs = InMemoryEditorPreferencesStore()
        prefs.update { it.copy(autosaveEnabled = false) }
        val smart = SmartEditorAutosaveController(FakeAutosaveController(3), prefs)

        assertEquals(0, smart.flushDirtySessions())
    }

    @Test
    fun `focus lost obeys preference`() = runTest {
        val prefs = InMemoryEditorPreferencesStore()
        prefs.update { it.copy(autosaveEnabled = true, autosaveOnFocusLost = false) }
        val smart = SmartEditorAutosaveController(FakeAutosaveController(2), prefs)

        assertEquals(0, smart.onFocusLost())
    }

    private class FakeAutosaveController(
        private val returns: Int,
    ) : EditorAutosaveController {
        override suspend fun flushDirtySessions(): Int = returns
    }
}
