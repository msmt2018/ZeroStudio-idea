package android.zero.studio.editor.api

interface EditorAutosaveController {
    suspend fun flushDirtySessions(): Int
}
