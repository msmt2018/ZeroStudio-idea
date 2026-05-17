package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LimitedRecentFilesStoreTest {
    @Test
    fun `recordOpen should follow preference max size`() = runTest {
        val prefs = InMemoryEditorPreferencesStore()
        prefs.update { it.copy(maxRecentFiles = 2) }
        val store = LimitedRecentFilesStore(prefs)

        store.recordOpen("/a")
        store.recordOpen("/b")
        store.recordOpen("/c")

        assertEquals(listOf("/c", "/b"), store.recentFiles.value)
    }
}
