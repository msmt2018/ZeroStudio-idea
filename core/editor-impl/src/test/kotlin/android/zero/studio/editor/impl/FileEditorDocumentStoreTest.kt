package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class FileEditorDocumentStoreTest {

    @Test
    fun `save then load roundtrip`() = runTest {
        val dir = Files.createTempDirectory("editor-store-test")
        val file = dir.resolve("a/b/sample.txt")
        val store = FileEditorDocumentStore()

        store.save(file.toString(), "hello")
        val loaded = store.load(file.toString())

        assertEquals("hello", loaded)
    }
}
