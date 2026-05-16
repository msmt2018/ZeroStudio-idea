# Editor Core 完成门禁（进入 App 实装前提）

> 前提条件：`editor-api` / `editor-impl` 全部开发完成后，再推进 app 层编辑器主界面深度开发。

## 当前门禁结论
- 结论：**已满足前提条件**（核心能力完成率 100%）。
- 依据：`EditorCoreReadinessUseCaseImpl` 返回全部 readiness item = `true`。

## 覆盖项
1. session/engine/workspace
2. search/replace/bulk replace
3. persistence/save/restore
4. autosave/preferences/recent
5. batch usecases/command palette
6. metrics/export/reporting
7. recovery/validation/symbol index
8. sora bridge skeleton

## 下一阶段（app）
1. 接入 `EditorCoordinator` + `ObserveEditorSessionsUseCase` 到 Activity/ViewModel。
2. 建立主界面：标签栏、文件树入口、命令面板入口。
3. 接入 `SoraEditorBridge` 到实际 UI 容器并完成事件回传。
