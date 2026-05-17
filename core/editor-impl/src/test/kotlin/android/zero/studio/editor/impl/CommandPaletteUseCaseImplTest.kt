package android.zero.studio.editor.impl

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommandPaletteUseCaseImplTest {

    @Test
    fun `list should expose built-in commands`() {
        val useCase = CommandPaletteUseCaseImpl(
            saveDirtyEditorsUseCase = { 0 },
            closeAllEditorsUseCase = { 0 },
        )

        val items = useCase.list()
        assertEquals(2, items.size)
        assertEquals("editor.save_dirty", items[0].id)
    }

    @Test
    fun `execute should dispatch commands`() = runTest {
        var saved = 0
        var closed = 0
        val useCase = CommandPaletteUseCaseImpl(
            saveDirtyEditorsUseCase = { saved += 1; 1 },
            closeAllEditorsUseCase = { closed += 1; 1 },
        )

        assertTrue(useCase.execute("editor.save_dirty"))
        assertTrue(useCase.execute("editor.close_all"))
        assertFalse(useCase.execute("unknown"))

        assertEquals(1, saved)
        assertEquals(1, closed)
    }
}
