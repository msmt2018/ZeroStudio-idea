package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorDocumentMeta
import android.zero.studio.editor.api.LineEnding

internal fun testMeta() = EditorDocumentMeta(
    filePath = null,
    languageId = "plaintext",
    charset = "UTF-8",
    lineEnding = LineEnding.LF,
)
