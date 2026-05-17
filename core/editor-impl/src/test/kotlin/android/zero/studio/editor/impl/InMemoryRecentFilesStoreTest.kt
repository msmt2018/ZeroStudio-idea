package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryRecentFilesStoreTest {

    @Test
    fun `recordOpen should move existing path to front`() = runTest {
        val store = InMemoryRecentFilesStore(maxSize = 5)
        store.recordOpen("/a")
        store.recordOpen("/b")
        store.recordOpen("/a")

        assertEquals(listOf("/a", "/b"), store.recentFiles.value)
    }

    @Test
    fun `recordOpen should respect max size`() = runTest {
        val store = InMemoryRecentFilesStore(maxSize = 2)
        store.recordOpen("/a")
        store.recordOpen("/b")
        store.recordOpen("/c")

        assertEquals(listOf("/c", "/b"), store.recentFiles.value)
    }
}
