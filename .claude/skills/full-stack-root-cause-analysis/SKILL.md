---
name: full-stack-root-cause-analysis
description: Use when a bug spans frontend and backend — UI display issues, data mismatch, type errors, NaN, undefined values, or form field problems. Before proposing any fix, investigate the full data chain: backend entity/DTO → API response → frontend type/interface → component rendering.
---

# 全栈根因分析

## 概述

单侧排查是 bug 久治不愈的首要原因。前端改了又改还是错，往往因为问题在后端返回的数据结构。

**核心原则：修 bug 前必须看完数据链路的每一层，不许只看一侧就动手。**

## 铁律

```
没查完前后端两侧的数据流，不许提修复方案
```

只看了前端就改组件？删掉。从头来。

## 数据链路检查清单

遇到前后端交互 bug 时，**按顺序**检查以下每一层：

### 第 1 层：后端数据源

- [ ] 实体/枚举定义（字段类型、code 值是数字还是字符串）
- [ ] DTO 字段类型和序列化注解（`@JsonFormat`、`ToStringSerializer`）
- [ ] 服务层返回值构建逻辑

### 第 2 层：API 传输

- [ ] 请求/响应类型定义（前端 TypeScript interface vs 后端 Java class）
- [ ] 响应拦截器是否转换数据（unwrap Result 包装、字段重命名）
- [ ] 实际网络响应（DevTools Network 面板或 console.log）

### 第 3 层：前端消费

- [ ] 类型定义是否与后端实际返回一致
- [ ] 组件 props 的值转换逻辑（`Number()`、`String()`、类型强转）
- [ ] UI 组件对值类型的约束（`ElSelect` 用 `===` 严格匹配）

## 典型反模式

### 反模式 1：只看前端就改组件

```
症状：Select 显示 NaN
错误做法：换 ElSelectV2 → ElSelect，改 v-model 绑定，加 watchEffect
正确做法：先 console.log 看选项的 value 是什么 → 发现是 "UNMARRIED"
         → 查后端枚举定义 → code 是字符串 → 去掉 Number() 转换
```

### 反模式 2：假设后端返回格式

```
症状：表单回显空白
错误做法：假设后端返回数字，前端加 Number() 转换
正确做法：查后端 DTO → 确认字段类型 → 按实际类型处理
```

### 反模式 3：改了前端没效果就继续改前端

```
第一次改完还错 → 应该怀疑根因判断，不是继续在前端找
连续两次改同一侧都没效果 → 必须切换到另一侧排查
```

## 快速排查流程

```dot
digraph full_stack_debug {
    rankdir=TB;
    node [shape=box];

    发现问题;
    加日志确认数据;
    数据正确? [shape=diamond];
    查前端组件逻辑;
    查后端返回数据;
    后端数据正确? [shape=diamond];
    查后端生成逻辑;
    查前端类型转换;
    定位根因;
    验证修复;

    发现问题 -> 加日志确认数据;
    加日志确认数据 -> 数据正确?;
    数据正确 -> 查前端组件逻辑 [label="是"];
    数据正确 -> 查后端返回数据 [label="否"];
    查后端返回数据 -> 后端数据正确?;
    后端数据正确 -> 查前端类型转换 [label="是"];
    后端数据正确 -> 查后端生成逻辑 [label="否"];
    查前端组件逻辑 -> 定位根因;
    查前端类型转换 -> 定位根因;
    查后端生成逻辑 -> 定位根因;
    定位根因 -> 验证修复;
}
```

## Whale HIS 项目速查

本项目的关键数据链路：

| 层级 | 文件位置 | 关键点 |
|------|----------|--------|
| 枚举定义 | `whale-health/health-domain-shared/.../enums/` | `code` 是 String，如 `"UNMARRIED"`、`"A"` |
| 实体 | `whale-health/health-domain/.../entity/` | 字段类型、`nullable` 约束 |
| DTO | `whale-health/health-application/.../dto/` | `@JsonFormat`、`ToStringSerializer` |
| 请求类 | `whale-health/health-application/.../request/` | `@NotBlank`、`@NotNull` 校验 |
| API 注册 | `whale-framework/whale-web/.../AppServiceControllerRegistrar.java` | 自动注册 `POST /api/app/patient/{method}` |
| 响应包装 | `whale-framework/whale-web/.../ApiResponseWrapper.java` | `Result.success(data)` 包装 |
| 前端请求 | `openhis-pro-ui/apps/web-ele/src/api/` | `responseReturn: 'data'` 自动 unwrap |
| 前端类型 | `openhis-pro-ui/apps/web-ele/src/api/basic/patient.ts` | TypeScript interface |
| 表单组件 | `openhis-pro-ui/apps/web-ele/src/views/basic/patient/` | `useVbenForm` + `ElSelect` |

## 常见错误

| 错误 | 后果 | 正确做法 |
|------|------|---------|
| 对字符串 code 做 `Number()` | `NaN` | 先确认后端返回类型 |
| 只改前端不查后端 | 改了 N 版还是错 | 先查后端 DTO 和枚举 |
| 假设枚举值是数字 | 类型不匹配 | 查枚举定义文件 |
| 连续改同一侧 3 次 | 浪费时间 | 切换到另一侧排查 |
