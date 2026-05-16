package android.zero.studio.editor.api

data class CommandPaletteItem(
    val id: String,
    val title: String,
    val shortcut: KeyStroke? = null,
)

interface CommandPaletteUseCase {
    fun list(): List<CommandPaletteItem>
    suspend fun execute(commandId: String): Boolean
}
