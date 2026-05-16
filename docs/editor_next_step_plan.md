# Editor Core 下一步工作计划（Phase 0.2）

## 当前状态
- 已有 `editor-api` + `editor-impl` 基础模型。
- 已有内存会话、命令模型、workspace、多会话切换、undo/redo。
- 已有基础单元测试（set text、undo/redo、replace、save）。

## 下一步目标（本周）
1. 完成编辑器“可用性 API”
   - [x] 会话内全文搜索 `find(query, options)`
   - [x] 跳转行列 `goTo(line, column)`（可复用 MoveCursor）
   - [x] 文本统计接口（行数、字符数）
2. 可靠性增强
   - [x] close 后调用防御测试
   - [x] 边界 case（空文本、超大 range、空 query）测试
3. 与 UI 对接准备
   - [x] 增加 `EditorViewAdapter` 接口草案（为 Sora 适配做桥接）
   - [x] 增加 `EditorCommand` -> 快捷键映射表

## 第二阶段（下周）
1. Sora 适配器 `SoraEditorEngine`（editor-impl 内新增）
2. 文档会话持久化层（file IO + charset + lineEnding）
3. 增量高亮 Provider 接口（先定义 API，后接 treesitter）

## 里程碑验收标准
- API 稳定：session/workspace/search 三大能力具备。
- 单元测试数量 >= 12，覆盖核心边界路径。
- 可切换 InMemory/Sora 两种 engine（依赖注入方式）。
