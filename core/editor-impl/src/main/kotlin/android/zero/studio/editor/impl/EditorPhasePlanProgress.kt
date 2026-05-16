package android.zero.studio.editor.impl

data class PlanCheckpoint(
    val key: String,
    val done: Boolean,
)

class EditorPhasePlanProgress {
    fun checkpoints(): List<PlanCheckpoint> = listOf(
        PlanCheckpoint("step_2_3_recent", true),
        PlanCheckpoint("step_2_4_session_list", true),
        PlanCheckpoint("step_2_5_preferences", true),
        PlanCheckpoint("step_2_6_workspace_search", true),
        PlanCheckpoint("step_2_7_batch", true),
        PlanCheckpoint("step_2_8_metrics", true),
        PlanCheckpoint("step_2_9_recovery", true),
        PlanCheckpoint("step_3_0_command_palette", true),
        PlanCheckpoint("step_3_1_snapshot_report", true),
        PlanCheckpoint("step_3_2_validation", true),
    )

    fun completionRate(): Double {
        val list = checkpoints()
        if (list.isEmpty()) return 0.0
        val done = list.count { it.done }
        return done.toDouble() / list.size
    }
}
