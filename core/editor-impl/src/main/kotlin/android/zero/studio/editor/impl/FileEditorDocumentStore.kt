package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorDocumentStore
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class FileEditorDocumentStore : EditorDocumentStore {
    override suspend fun load(path: String, charset: String): String {
        return Files.readString(Path.of(path), Charset.forName(charset))
    }

    override suspend fun save(path: String, content: String, charset: String) {
        val target = Path.of(path)
        target.parent?.let { Files.createDirectories(it) }
        Files.writeString(target, content, Charset.forName(charset))
    }
}
