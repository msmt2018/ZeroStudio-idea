package android.zero.studio.editor.impl

import android.zero.studio.editor.api.EditorSession
import android.zero.studio.editor.api.EditorSnapshot
import android.zero.studio.editor.api.EditorViewAdapter

/**
 * Sora 适配桥（无三方依赖版骨架）。
 *
 * 说明：当前仓库尚未引入 Sora 依赖，此类先定义“桥接生命周期与状态同步协议”，
 * 后续接入 Sora 时只需在 [render] 中替换为真实的 Sora API 调用。
 */
class SoraEditorBridge : EditorViewAdapter {
    private var boundSession: EditorSession? = null

    override fun bind(session: EditorSession) {
        boundSession = session
        applySnapshot(session.state.value)
    }

    override fun unbind() {
        boundSession = null
    }

    override fun applySnapshot(snapshot: EditorSnapshot) {
        render(snapshot)
    }

    private fun render(snapshot: EditorSnapshot) {
        // TODO: 接入 io.github.Rosemoe.sora.widget.CodeEditor
        // 1) 同步文本
        // 2) 同步光标
        // 3) 同步选择区
        // 4) 根据 documentMeta.languageId 切换语言高亮 provider
        @Suppress("UNUSED_VARIABLE")
        val noop = snapshot
    }
}
