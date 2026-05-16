package android.zero.studio.editor.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorCoreReadinessUseCaseImplTest {
    @Test
    fun `report should be fully ready`() {
        val report = EditorCoreReadinessUseCaseImpl().report()
        assertTrue(report.isReady)
        assertEquals(1.0, report.completionRate)
        assertEquals(8, report.items.size)
    }
}
