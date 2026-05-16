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


### Step 1.5：会话持久化前置（已启动）
1. [x] 新增 `EditorDocumentStore` 持久化抽象。
2. [x] 新增 `FileEditorDocumentStore` 文件读写实现（JVM 参考版）。
3. [x] 新增 `PersistentEditorSessionFactory`，支持从路径加载会话。


### Step 1.6：Workspace 打开文件链路（已启动）
1. [x] 新增 `PersistentEditorWorkspace`，支持 `newSession(filePath)` 直接打开文件。
2. [x] 新增 workspace 级测试，校验打开/激活/关闭链路。


### Step 1.7：保存链路落盘（已启动）
1. [x] 新增 `PersistentEditorSession`，在 `save()` 时将文本写回文件系统。
2. [x] `PersistentEditorSessionFactory` 返回可落盘 session。
3. [x] 新增保存落盘单测（factory -> session -> save -> file）。


### Step 1.8：换行符规范化策略（已启动）
1. [x] 新增 `DocumentTextNormalizer`，统一 LF/CRLF 规范化。
2. [x] `PersistentEditorSession.save()` 落盘前按文档元信息规范化。
3. [x] 新增规范化与保存链路测试。


### Step 1.9：Workspace 管理门面（已启动）
1. [x] 新增 `EditorWorkspaceManager` 统一 open/save 操作入口。
2. [x] 新增 `EditorWorkspaceManagerImpl`（openFile/newScratch/saveActive/saveAll）。
3. [x] 新增 manager 层测试覆盖活跃会话保存与批量保存。


### Step 2.0：应用层用例封装（已启动）
1. [x] 新增 `OpenEditorUseCase` / `SaveEditorUseCase`，屏蔽 UI 对 workspace 细节的直接依赖。
2. [x] 新增 usecase 实现与测试，为后续 ViewModel 层接入做准备。


### Step 2.1：ViewModel 协调层前置（已启动）
1. [x] 新增 `EditorCoordinator` / `EditorUiState`，统一 UI 观察状态。
2. [x] 新增 `EditorCoordinatorImpl`，串联 open/save usecases 与 workspace。
3. [x] 新增 coordinator 测试，校验 open/save 后 uiState 刷新。


### Step 2.2：自动保存控制器（已启动）
1. [x] 新增 `EditorAutosaveController` 抽象。
2. [x] 新增 `EditorAutosaveControllerImpl`，支持仅保存 dirty 且 file-backed 会话。
3. [x] 新增 autosave 测试，覆盖 dirty/clean/scratch 三类会话。


### Step 2.3：最近打开文件追踪（已启动）
1. [x] 新增 `EditorRecentFilesStore` 抽象与内存实现。
2. [x] 新增 `TrackingOpenEditorUseCase`，在 open 成功后记录最近文件。
3. [x] 新增 recent/track 测试，覆盖去重、置顶、容量限制。


### Step 2.4：会话列表/关闭/最近文件用例（已启动）
1. [x] 新增 `ObserveEditorSessionsUseCase`，为 UI 标签栏/侧栏提供会话状态列表。
2. [x] 新增 `CloseEditorSessionUseCase`，统一关闭会话入口。
3. [x] 新增 `RecentFilesUseCase`，支持 recent 列表展示与清空。
4. [x] 新增对应测试，覆盖 active/dirty、关闭、recent 清空。


### Step 2.5：编辑器偏好与策略门控（已启动）
1. [x] 新增 `EditorPreferencesStore` 与 `EditorPreferences`（自动保存、recent 容量等）。
2. [x] 新增 `SmartEditorAutosaveController`，按偏好门控 autosave 触发。
3. [x] 新增 `LimitedRecentFilesStore`，按偏好动态限制 recent 列表容量。
4. [x] 新增偏好/自动保存/recent 策略测试。


### Step 2.6：工作区搜索 + 仪表盘聚合（已启动）
1. [x] 新增 `SearchInWorkspaceUseCase`，支持跨会话搜索聚合结果。
2. [x] 新增 `EditorDashboardAssembler`，一次性聚合 coordinator/sessions/recent 状态。
3. [x] 新增对应测试，覆盖搜索过滤与仪表盘快照。


### Step 2.7：批处理用例（已启动）
1. [x] 新增 `OpenManyEditorsUseCase`，支持多文件批量打开（去重+失败收集）。
2. [x] 新增 `SaveDirtyEditorsUseCase`，批量触发 dirty 会话保存。
3. [x] 新增 `CloseAllEditorsUseCase`，统一关闭全部会话。
4. [x] 新增批处理测试，覆盖成功/失败/真实落盘。


### Step 2.8：编辑器指标与可观测性（已启动）
1. [x] 新增 `EditorMetricsCollector` 与 `EditorMetricsUseCase`。
2. [x] 为批处理与 autosave 增加 metrics 装饰器。
3. [x] 新增 metrics 单测，覆盖记录/快照/重置与装饰器行为。


### Step 2.9：会话恢复机制（已启动）
1. [x] 新增 `EditorRecoveryStore` 与 `RecoverySnapshot`。
2. [x] 新增 `CaptureRecoveryUseCase` / `RestoreRecoveryUseCase`。
3. [x] 新增 recovery 测试，覆盖捕获与重建会话。
