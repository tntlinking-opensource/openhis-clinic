---
name: oh-cloudclinic-context
description: oh-cloudclinic (云诊所) 项目整体上下文——技术栈、架构、模块划分、代码约定。当修改该项目代码时自动适用。
---

# oh-cloudclinic 项目上下文

## 项目简介

oh-cloudclinic（云诊所）是一个全栈医疗诊所管理系统，包含 Spring Boot 后端和 Vue 2 前端。

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Spring Boot 2.1.0, Java 8, MyBatis-Plus 3.5.0, Shiro 1.4.0, JWT |
| 前端 | Vue 2 (Options API), Element UI 2.x, Vuex, Vue Router, Axios |
| 数据库 | MySQL（无迁移工具，手动 DDL） |
| 缓存 | Redis（shiro-redis 会话管理） |
| 构建 | Maven（后端）, Vue CLI 5 / Webpack（前端） |
| 报表 | UReport2, hiprint |

## 项目结构

```
Code/medical2.0/
  back/                 # Spring Boot 后端（端口 7016）
  client/               # Vue 2 前端（端口 7020，代理 /api → localhost:7016）
DB/                     # 数据库初始化 SQL
Doc/                    # 操作手册、功能说明
```

## 后端架构

### 分层约定

每个业务模块（`com.geeke.*`）遵循相同结构：`controller/`、`dao/`、`entity/`、`service/`。

**继承链：**
- Entity: `BaseEntity<T>` → `DataEntity<T>` → 具体实体（审计字段、逻辑删除、UUID 主键）
- DAO: `BaseDao` → `CrudDao<T>` → 具体 DAO（标准 CRUD 接口，SQL 在 XML mapper 中）
- Service: `BaseService` → `CrudService<D, T>` → 具体服务（`@Transactional(readOnly = true)` 默认）
- Controller: `BaseController` → 具体控制器（返回 `ResponseEntity<JSONObject>` via `ResultUtil.successJson()`）

### REST 接口规范

```
GET  /{module}/{entity}/{id}     # 单条查询
POST /{module}/{entity}/list     # 分页查询（SearchParams）
POST /{module}/{entity}/listAll  # 全量查询
POST /{module}/{entity}/save     # 新增/更新
POST /{module}/{entity}/delete   # 删除（逻辑删除）
POST /{module}/{entity}/bulk*    # 批量操作
```

### 业务模块

| 包 | 职责 |
|----|------|
| admin | 用户/角色/权限管理（RBAC） |
| outpatient | 挂号、病历、处方、医生工作台 |
| stock | 库存、药品、供应商、发药 |
| toll | 收费/计费管理 |
| cure | 治疗/检查管理 |
| member | 患者/会员管理 |
| org | 组织/诊所/科室 |
| schedule | 排班管理 |
| gen | 代码生成（Freemarker 模板） |
| sys | 系统配置/字典 |
| config | Spring/Shiro/Redis/Swagger 配置 |
| common | 公共工具、基础类 |

### 代码生成

`com.geeke.gen` 包提供完整的代码生成引擎：通过数据库表元数据自动生成 entity/DAO/service/controller/mapper XML + 前端组件。配置在 `application.yml` 的 `genConfigure` 下。

## 前端架构

### 动态路由

路由完全由后端驱动。登录后后端返回授权菜单/路由定义（`code`、`url`、`properties`），前端通过 `router.addRoute()` 动态注册。路由组件映射到 `@/views/<code>/index.vue`。

### 视图约定

- `views/<module>/index.vue` — 列表页
- `<module>Form.vue` — 编辑表单
- `metadata.js` — 数据驱动配置（表 ID、方案 ID）
- 列表页通过 `extends: MainUI` 继承基础 UI 能力（列管理、查询表单、加载状态）

### API 调用模式

`src/api/<module>/` 下按模块组织，每个文件导出命名函数：
- `listXxxPage`（POST）、`getXxxById`（GET）、`saveXxx`（POST）、`deleteXxx`（POST）

### 状态管理

Vuex 较薄，主要管理 UI 状态（主题、菜单、面包屑、加载）。数据获取直接在组件中通过 API 调用。

### 认证

所有会话状态（token、用户、路由、权限、公司）存储在 `sessionStorage` 中，通过 `src/utils/auth.js` 管理。

## 构建命令

```bash
# 后端
cd Code/medical2.0/back
mvn clean package -P dev       # 构建（dev/test/prod profile）
mvn spring-boot:run            # 直接运行

# 前端
cd Code/medical2.0/client
npm install
npm run dev                    # 开发服务器（端口 7020）
npm run build                  # 生产构建
npm run lint                   # ESLint
```

## 配置 Profile

| Profile | 端口 | 数据库 | 说明 |
|---------|------|--------|------|
| dev | 7016 | localhost:3306/md_yzs | 本地开发 |
| demo | 7016 | 远程 MySQL | 默认激活 |
| test | 9502 | 测试服务器 | 含 RabbitMQ |
| prod | 7016 | 生产服务器 | 含 RabbitMQ/微信/企信 |

## 关键约定

- **无数据库迁移工具**：Schema 变更通过手动 DDL 执行
- **逻辑删除**：`del_flag = 1`，不物理删除
- **主键生成**：UUID（`IdGen.uuid()`），非自增
- **MyBatis XML**：SQL 写在 `src/main/resources/mapper/` 下的 XML 文件中
- **系统级 JAR**：`lib/camundaex-1.0.0.jar`（工作流）、`lib/uid-generator-1.0.0-SNAPSHOT.jar`（分布式 ID）
- **前端 CDN 外置**：Vue、Element UI、ECharts 等通过 CDN 加载，不打包
- **样式修改**：使用 `/deep/` 深度选择器（项目使用 node-sass）
