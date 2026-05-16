# Editor Phase 1：Sora 适配实施计划

## 目标
将当前 `editor-api` / `editor-impl` 的会话能力真正绑定到 Sora 编辑器视图，实现可交互编辑界面。

## 分阶段任务

### Step 0：桥接骨架与可测试性（已完成）
1. [x] 引入 `SoraEditorBridge` 生命周期管理（bind/unbind）。
2. [x] 增加状态订阅与渲染注入点（`EditorRenderer`）。
3. [x] 增加上行事件入口（text/cursor/save）。
4. [x] 增加桥接层单元测试（`SoraEditorBridgeTest`）。


### Step 1：依赖接入与模块边界
1. 在 `:core:editor-impl` 增加 Sora 依赖（仅 impl 层依赖，api 层保持纯净）。
2. 保持 `EditorViewAdapter` 不变，新增 `SoraEditorBridge` 真实实现。

### Step 2：状态下行同步（Session -> Sora）
1. 文本同步：`EditorSnapshot.text` -> Sora document。
2. 光标同步：`EditorSnapshot.cursor` -> Sora cursor。
3. 选择区同步：`EditorSnapshot.selection` -> Sora selection。

### Step 3：事件上行同步（Sora -> Session）
1. 监听文本变更并转发为 `EditorCommand`。
2. 监听光标移动并转发为 `goTo`/`MoveCursor`。
3. 监听选择变化并转发为 `SetSelection`。

### Step 4：稳定性与性能
1. 增量更新，避免整文档反复 setText。
2. 防抖：输入高频场景下合并事件。
3. 大文件阈值策略：降级高亮、延迟统计更新。

## 验收标准
- 能在 UI 中打开 session 并实时编辑。
- Undo/Redo/Save/Find 与快捷键映射可通过 UI 触发。
- 文本输入时无明显卡顿（基础可用）。
