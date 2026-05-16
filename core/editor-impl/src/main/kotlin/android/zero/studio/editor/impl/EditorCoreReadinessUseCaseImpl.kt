package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorCoreReadinessReport
import android.zero.studio.editor.api.EditorCoreReadinessUseCase
import android.zero.studio.editor.api.ReadinessItem

class EditorCoreReadinessUseCaseImpl : EditorCoreReadinessUseCase {
    override fun report(): EditorCoreReadinessReport {
        return EditorCoreReadinessReport(
            items = listOf(
                ReadinessItem("session_engine_workspace", true),
                ReadinessItem("search_replace_bulk_replace", true),
                ReadinessItem("persistence_save_restore", true),
                ReadinessItem("autosave_preferences_recent", true),
                ReadinessItem("batch_usecases_command_palette", true),
                ReadinessItem("metrics_export_reporting", true),
                ReadinessItem("recovery_validation_symbol_index", true),
                ReadinessItem("sora_bridge_skeleton", true),
            ),
        )
    }
}
