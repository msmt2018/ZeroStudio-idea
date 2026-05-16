# ZeroStudio Android IDE 全面设计与分阶段研发方案

> 项目定位：面向 Android 端的开发工具（IDE），以 **Gradle 构建支持 + Bazel 为核心能力**，并通过插件化与模块化架构实现可持续演进。

## 1. 产品目标与边界

### 1.1 产品目标
- 在 Android 设备上提供“可用、可扩展、可演进”的开发环境。
- 第一阶段优先完成：**终端 + 编辑器核心界面 + 文件树 + 主界面框架**。
- 构建系统方向：支持 Gradle；中长期把 Bazel 作为核心构建编排能力。

### 1.2 技术边界（当前阶段）
- 不自研编辑器核心，采用 **Sora Editor**。
- 语法高亮采用 Tree-sitter 技术路线，逐步引入 NDK + C/C++ + Rust 混合能力。
- 不自建 APT 仓库；终端系统采用“像 Termux 一样在应用数据目录内管理 rootfs”的方案。

---

## 2. 总体架构（满足 MVVM + ViewModel + Compose + 懒加载 + 异步 + 事件订阅 + 模块化 + 插件化）

## 2.1 包名与语言约束
- 主包名：`android.zero.studio`
- 主语言：Kotlin
- 扩展语言：C/C++（NDK）、Rust（Tree-sitter/高性能组件）、必要时 LLVM 工具链适配。

## 2.2 分层架构
- **app-shell（壳层）**：导航、窗口、主题、权限桥接、插件装配。
- **domain（领域层）**：命令模型、项目模型、文件模型、插件生命周期。
- **data（数据层）**：本地数据库、配置、文件系统访问、rootfs 元数据。
- **feature（功能层）**：终端、编辑器、文件树、构建面板、设置等。
- **plugin（插件层）**：侧栏页面插件、Action 插件、语言支持插件。
- **native（原生层）**：pty / shell bridge、Tree-sitter、性能关键路径。

## 2.3 UI 与交互架构
- UI 框架：Material 3 + Compose。
- 主界面：`Scaffold + NavigationRail/Drawer + Editor/Terminal Split`。
- 侧栏容器：左侧自定义 Drawer，插件注册侧栏页面。
- 动画规范：
  - 面板开合：`AnimatedVisibility + tween(180~240ms)`
  - 编辑器/终端切换：`Crossfade`
  - 文件树展开：`animateContentSize`

## 2.4 异步与事件机制
- Kotlin Coroutines + Flow/StateFlow。
- 线程模型：
  - IO：文件读写、rootfs 解压、插件扫描
  - Default：索引/语法树解析
  - Main：UI 状态分发
- 事件总线建议：基于 `SharedFlow<DomainEvent>` 实现订阅线程，避免全局粘性事件滥用。

---

## 3. 模块化与插件化设计

## 3.1 建议模块结构（Monorepo）
- `:app`
- `:core:common`（Result、Dispatcher、日志、错误模型）
- `:core:ui`（M3 主题、基础组件）
- `:core:plugin-api`（插件接口）
- `:core:action-api`（命令/动作接口）
- `:core:editor-api`（编辑器抽象，当前由 Sora 适配）
- `:feature:home`
- `:feature:filetree`
- `:feature:editor`
- `:feature:terminal`
- `:feature:workspace`
- `:native:pty`
- `:native:treesitter`

## 3.2 侧栏插件系统（Fragment 容器核心）
你要求侧栏以 Fragment 容器作为核心，建议如下：

### 核心接口
- `SidebarPlugin`：
  - `id: String`
  - `title: String`
  - `iconRes: Int`
  - `order: Int`
  - `createFragment(): Fragment`
  - `onActivate()/onDeactivate()`

### 注册中心
- `SidebarPluginRegistry`
  - 支持启动时注册、动态启用/禁用、排序。
  - 提供 `Flow<List<SidebarPluginMeta>>` 供 UI 观察。

### 容器管理
- `SidebarHostFragment`
  - 仅负责插件 Fragment 的 attach/show/hide。
  - 使用 `FragmentManager` 缓存实例，避免重复创建。

### 与 Compose 协同
- Compose 渲染侧栏按钮和状态。
- 选中插件后将命令下发到 `SidebarHostFragment` 执行切换。

## 3.3 Action 插件系统
- `IdeAction`：`id`, `title`, `shortcut`, `execute(context)`
- `ActionRegistry`：统一注册与查询。
- `ActionDispatcher`：命令触发入口（按钮、命令面板、快捷键）。
- 插件可批量注册 action（如“新建文件”“格式化”“运行任务”）。

---

## 4. 编辑器方案（基于 Sora）

## 4.1 目标能力（阶段化）
- P0：基础编辑（打开/保存/撤销重做/搜索）
- P1：多标签、行号、软换行、主题
- P2：Tree-sitter 语法高亮 + 基础符号导航
- P3：LSP 客户端桥接（补全/跳转/诊断）

## 4.2 编辑器架构
- `EditorFacade`：统一编辑器能力抽象，屏蔽 Sora 细节。
- `DocumentSession`：单文件会话（文本状态、脏标记、编码、换行符）。
- `EditorTabManager`：多标签与生命周期管理。
- `EditorCommandBus`：编辑命令流（可回放/可测试）。

---

## 5. Tree-sitter 集成方案（NDK + C/C++ + Rust）

## 5.1 集成原则
- 先实现 C API 路径（可用优先），再逐步引入 Rust 侧高阶能力。
- Android 端统一经 JNI 暴露：`parse`, `highlight`, `querySymbols`。

## 5.2 技术路线
1. 在 `:native:treesitter` 中引入 tree-sitter 核心与常见 grammar（C/C++/Rust/Kotlin/Java）。
2. 使用 CMake 构建 `.so`，当前首发仅产出并发布双 ABI：`arm64-v8a`、`armeabi-v7a`。
3. Kotlin 侧封装 `TreeSitterBridge`。
4. 与 Sora 的 token provider 对接，实现增量高亮。
5. 后续再将部分逻辑替换为 Rust 实现（通过 `cbindgen + JNI`）。

## 5.3 风险点
- ABI 体积膨胀（需按需下载 grammar 或拆分动态特性包）。
- 低端机解析耗时（需增量解析 + 后台线程 + 节流）。

---

## 6. 终端方案（rootfs：Ubuntu/Debian/Arch）

## 6.1 目标
- 在 `data/data/<package>/...` 内完成 rootfs 存储、解压与运行。
- 不依赖自建 apt 仓库。
- 支持多发行版 profile（ubuntu/debian/arch）。

## 6.2 架构组件
- `RootfsManager`：下载、校验、解压、版本管理。
- `DistroProfile`：发行版元数据（源地址、校验、启动命令模板）。
- `TerminalSessionManager`：多会话管理。
- `PtyBridge (native)`：伪终端创建、I/O 管道、信号处理。
- `TerminalRenderer`：终端 UI（字符网格、输入法桥接、复制粘贴）。

## 6.3 工作流（构建与分发）
1. 选择发行版 profile（Ubuntu/Debian/Arch）。
2. 下载 rootfs 压缩包（官方或可信镜像）。
3. 校验 SHA256。
4. 解压到应用私有目录：
   - `.../files/rootfs/<distro>/<version>/`
5. 准备启动脚本（挂载必要目录、环境变量、用户目录）。
6. 通过 proot/chroot 类方案启动 shell（取决于设备能力与权限策略）。
7. UI 侧展示 session，支持后台保活与恢复。

## 6.4 关键设计点
- 每个发行版维护独立环境，避免互相污染。
- 支持 rootfs 快照与回滚（升级失败可恢复）。
- 下载/解压全异步，提供进度与错误恢复。

---

## 7. Android 兼容性与版本策略

你的要求“最低 Android 5-6，最高 Android 17”建议拆分如下：
- **可行建议**：
  - `minSdk 23`（Android 6.0，兼容性与现代 API 平衡更好）
  - `targetSdk` 跟随最新稳定（当前以 Android 16/17 对应 API 为目标，按 Google 发布节奏迭代）
- 若必须触达 Android 5（API 21/22），需额外兼容：
  - 文件访问与权限行为差异
  - WebView/SSL 兼容问题
  - NDK 与三方库可用性下降

建议第一版先以 `minSdk 23` 落地，后续评估是否下探。

---


### 7.1 APK ABI 打包策略（补充）
- 首发安装包仅支持并发布：
  - `arm64-v8a`（arm64）
  - `armeabi-v7a`（arm-v7）
- 发布策略建议：
  - Google Play：使用 App Bundle + ABI split（自动分发）
  - 非商店渠道：提供双独立 APK（`arm64-v8a.apk` 与 `armeabi-v7a.apk`）
- Native 模块约束：
  - `:native:pty`、`:native:treesitter` 必须同时产出上述双 ABI，禁止只发单 ABI。
  - CI 增加 ABI 完整性检查，防止某个 `.so` 漏打包。

## 8. 分阶段研发路线图（重点：先做终端+编辑器主界面）

## 阶段 0（1~2 周）：工程基建
- 建立多模块骨架（app/core/feature/native/plugin-api）。
- 落地 CI：lint、单元测试、构建。
- 统一代码规范（ktlint/detekt）。

**交付物**
- 可编译空壳工程。
- 插件 API、Action API 初版接口。

## 阶段 1（2~4 周）：IDE 主界面框架
- 主界面 Scaffold。
- 左侧 Drawer + 侧栏插件注册/切换。
- 中央工作区容器（编辑器/终端占位）。
- 底部状态栏（项目路径、编码、行列、任务状态）。

**交付物**
- 可运行的 IDE Shell。
- 至少 3 个侧栏插件页（文件树、搜索、终端会话）。

## 阶段 2（3~5 周）：编辑器核心接入（Sora）
- Sora 集成与 `EditorFacade`。
- 打开/保存/撤销重做/搜索。
- 多标签与文档会话。

**交付物**
- 可编辑多文件。
- 文件树 -> 打开文件联动。

## 阶段 3（3~6 周）：终端核心可用
- PTY native bridge。
- rootfs 下载、解压、启动流程。
- Ubuntu/Debian/Arch 三 profile 初版。

**交付物**
- 能创建终端会话并执行命令。
- rootfs 安装向导。

## 阶段 4（4~8 周）：Tree-sitter 语法高亮
- native treesitter 模块。
- Kotlin JNI 桥。
- 与编辑器 token provider 对接。

**交付物**
- Kotlin/Java/C++/Rust 基础高亮。
- 增量解析性能可接受。

## 阶段 5（持续）：构建系统与插件生态
- Gradle Task 面板。
- Bazel 命令面板与运行配置。
- Action 插件市场雏形。

---

## 9. UI 设计规范（Material 3 + 动画）

## 9.1 视觉体系
- 深浅色主题 + 动态色（可关闭）。
- 字体分级：编辑器等宽字体，UI 使用系统 sans。
- 统一 4/8dp 间距系统。

## 9.2 主界面布局
- 左：插件侧栏（可折叠）
- 中：编辑器主区（多标签）
- 下：终端抽屉（可拉伸）
- 右（可选）：检查/诊断面板

## 9.3 动效基线
- 交互反馈 < 100ms
- 面板动画 180~240ms
- 减少大面积重绘，优先局部更新

---

## 10. 质量保障与工程治理

- 单元测试：domain/data/plugin registry/action dispatcher
- 集成测试：文件树-编辑器-保存链路、终端会话生命周期
- Native 测试：JNI 边界、内存泄露、崩溃回收
- 性能指标：
  - 首屏冷启动
  - 大文件打开耗时（1MB/5MB/20MB）
  - 语法高亮首包与增量延迟

---

## 11. 你现在就可以启动的“第一批任务清单”（可直接执行）

1. 初始化模块结构与基础依赖（Compose、Lifecycle、Navigation）。
2. 完成 `core:plugin-api` + `core:action-api` 接口定义。
3. 实现 `SidebarPluginRegistry` 与 `SidebarHostFragment`。
4. 主界面完成左侧插件栏 + 中央容器 + 底部状态栏。
5. 接入 Sora 并打通“文件树点击打开文件”最短链路。
6. 终端先做假会话（mock），验证 UI 与 session 管理流程。
7. 再接 rootfs manager 与 pty bridge 真实能力。

---

## 12. 风险与决策建议（关键）

- **风险 1：兼容 Android 5 成本高**
  - 建议：先以 Android 6+ 发布 MVP。
- **风险 2：Tree-sitter + 多 grammar 体积大**
  - 建议：分包/按需下载 grammar。
- **风险 3：终端 rootfs 方案设备差异大**
  - 建议：先锁定 arm64 + Android 8+ 验证，再下探。
- **风险 4：插件系统过早复杂化**
  - 建议：先做“内置插件化”，后做外部插件加载。

---

## 13. MVP 成功标准（第一里程碑）

- 可以创建/打开本地项目目录。
- 文件树可浏览并打开文件。
- 编辑器可编辑并保存。
- 可启动至少一个 rootfs 终端会话并运行基础命令。
- 侧栏插件能动态注册与切换。
- 整体 UI 符合 Material3 基线并具备关键动画。

> 如果你愿意，下一步我可以继续给出：
> 1) 模块级 `build.gradle.kts` 样板；
> 2) 插件 API 与 Action API 的 Kotlin 接口草案；
> 3) 第一阶段（阶段0~1）任务拆解到“按天执行”的甘特式计划。
