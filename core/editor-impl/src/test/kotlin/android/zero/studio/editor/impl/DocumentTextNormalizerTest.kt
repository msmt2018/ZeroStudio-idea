package android.zero.studio.editor.impl

import android.zero.studio.editor.api.LineEnding
import kotlin.test.Test
import kotlin.test.assertEquals

class DocumentTextNormalizerTest {

    @Test
    fun `normalize should convert mixed endings to LF`() {
        val input = "a\r\nb\rc\n"
        val output = DocumentTextNormalizer.normalizeLineEnding(input, LineEnding.LF)
        assertEquals("a\nb\nc\n", output)
    }

    @Test
    fun `normalize should convert mixed endings to CRLF`() {
        val input = "a\r\nb\rc\n"
        val output = DocumentTextNormalizer.normalizeLineEnding(input, LineEnding.CRLF)
        assertEquals("a\r\nb\r\nc\r\n", output)
    }
}
