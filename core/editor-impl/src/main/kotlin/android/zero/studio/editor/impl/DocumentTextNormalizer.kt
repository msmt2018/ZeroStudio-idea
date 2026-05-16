package android.zero.studio.editor.impl

import android.zero.studio.editor.api.LineEnding

object DocumentTextNormalizer {
    fun normalizeLineEnding(text: String, target: LineEnding): String {
        val unified = text.replace("\r\n", "\n").replace("\r", "\n")
        return when (target) {
            LineEnding.LF -> unified
            LineEnding.CRLF -> unified.replace("\n", "\r\n")
        }
    }
}
