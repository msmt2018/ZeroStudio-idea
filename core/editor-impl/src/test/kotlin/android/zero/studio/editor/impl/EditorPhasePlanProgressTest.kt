package android.zero.studio.editor.impl

import kotlin.test.Test
import kotlin.test.assertEquals

class EditorPhasePlanProgressTest {
    @Test
    fun `completion should be 100 percent for declared checkpoints`() {
        val progress = EditorPhasePlanProgress()
        assertEquals(1.0, progress.completionRate())
        assertEquals(10, progress.checkpoints().size)
    }
}
