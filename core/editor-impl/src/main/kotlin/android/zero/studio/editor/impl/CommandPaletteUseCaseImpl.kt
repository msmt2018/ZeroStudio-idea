package android.zero.studio.editor.impl

import android.zero.studio.editor.api.CloseAllEditorsUseCase
import android.zero.studio.editor.api.CommandPaletteItem
import android.zero.studio.editor.api.CommandPaletteUseCase
import android.zero.studio.editor.api.EditorKey
import android.zero.studio.editor.api.KeyModifier
import android.zero.studio.editor.api.KeyStroke
import android.zero.studio.editor.api.SaveDirtyEditorsUseCase

class CommandPaletteUseCaseImpl(
    private val saveDirtyEditorsUseCase: SaveDirtyEditorsUseCase,
    private val closeAllEditorsUseCase: CloseAllEditorsUseCase,
) : CommandPaletteUseCase {
    override fun list(): List<CommandPaletteItem> = listOf(
        CommandPaletteItem(
            id = CMD_SAVE_DIRTY,
            title = "Save Dirty Editors",
            shortcut = KeyStroke(EditorKey.S, setOf(KeyModifier.CTRL, KeyModifier.SHIFT)),
        ),
        CommandPaletteItem(
            id = CMD_CLOSE_ALL,
            title = "Close All Editors",
        ),
    )

    override suspend fun execute(commandId: String): Boolean {
        return when (commandId) {
            CMD_SAVE_DIRTY -> {
                saveDirtyEditorsUseCase()
                true
            }
            CMD_CLOSE_ALL -> {
                closeAllEditorsUseCase()
                true
            }
            else -> false
        }
    }

    companion object {
        const val CMD_SAVE_DIRTY = "editor.save_dirty"
        const val CMD_CLOSE_ALL = "editor.close_all"
    }
}
