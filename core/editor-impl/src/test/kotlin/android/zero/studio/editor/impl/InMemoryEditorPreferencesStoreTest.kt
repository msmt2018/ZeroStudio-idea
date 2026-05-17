package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class InMemoryEditorPreferencesStoreTest {
    @Test
    fun `update should mutate preferences`() = runTest {
        val store = InMemoryEditorPreferencesStore()
        store.update { it.copy(autosaveEnabled = false, maxRecentFiles = 5) }

        assertFalse(store.preferences.value.autosaveEnabled)
        assertEquals(5, store.preferences.value.maxRecentFiles)
    }
}
