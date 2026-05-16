# Editor Core 进度统计与未完成工作报告（截至 2026-05-16）

## 1) 总体进度概览

- 当前阶段：**Phase 0.2（Editor Core）进行中**。
- 统计口径：按 `docs/editor_next_step_plan.md` 的任务项逐条核对。
- 任务总数：7 项（本周目标）。
- 已完成：1 项。
- 未完成：6 项。
- 完成率：**14.3%**（1/7）。

---

## 2) 已完成工作

1. ✅ 会话内全文搜索 `find(query, options)`
   - API：`EditorSession.find(...)` 已定义。
   - 实现：`InMemoryEditorSession.find(...)` 已完成。
   - 测试：大小写不敏感/敏感两条用例已补充。

---

## 3) 未完成工作清单（按优先级）

### P0（本周必须完成）
1. ⏳ `goTo(line, column)` 语义化 API（目前仅有 `MoveCursor` 命令）。
2. ⏳ 文本统计接口（行数、字符数）及测试。
3. ⏳ close 后调用防御测试（`execute/find/save` 在 close 后应抛错）。
4. ⏳ 边界 case 测试补齐：
   - 空文本
   - 超大/越界 range
   - 空 query

### P1（下周优先）
5. ⏳ `EditorViewAdapter` 接口草案（Sora 适配桥）。
6. ⏳ `EditorCommand` -> 快捷键映射表（命令系统联动）。

---

## 4) 风险与阻塞

1. **测试执行阻塞**：仓库当前无 `./gradlew` wrapper，无法在标准命令下直接跑测试流水。
2. **API 演进风险**：在 Sora 适配前，`EditorSession` 接口仍可能小幅调整。
3. **质量风险**：边界 case 未补齐前，不建议进入 UI 联调阶段。

---

## 5) 下一步执行计划（建议顺序）

### 第一步（今天）
- 补 `close` 防御测试 + 边界 case 测试，目标测试总数提升到 10+。

### 第二步（明天）
- 增加 `goTo` 语义化 API（可内部复用 `MoveCursor`），并补对应测试。

### 第三步（本周内）
- 增加文本统计接口（字符数/行数）并验证换行边界。

### 第四步（下周开始）
- 进入 `EditorViewAdapter` 与 `SoraEditorEngine` 桥接设计。

---

## 6) 里程碑达成预测

- 若按上述节奏推进：
  - 2 天内：本周 P0 项预计可完成 80%+。
  - 1 周内：可进入 Sora 适配前置阶段。
