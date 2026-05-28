# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

oh-cloudclinic (云诊所) is a full-stack medical clinic management system. The source lives under `Code/medical2.0/` with a Spring Boot backend (`back/`) and Vue 2 frontend (`client/`).

## Build & Run Commands

### Backend (Java 8, Maven)
```bash
cd Code/medical2.0/back
mvn clean package                    # Build with default dev profile
mvn clean package -P test            # Test profile
mvn clean package -P prod            # Production profile
mvn spring-boot:run                  # Run directly (port 7016)
```

### Frontend (Node, npm)
```bash
cd Code/medical2.0/client
npm install
npm run dev                          # Dev server on port 7020 (proxies /api to localhost:7016)
npm run build                        # Production build
npm run lint                         # ESLint
```

## Architecture

### Backend (`Code/medical2.0/back/`)

- **Spring Boot 2.1.0** / Java 8, packaged as executable JAR (`com.geeke:gserver`)
- **Entry point:** `com.geeke.MyApplication`
- **ORM:** MyBatis-Plus 3.5.0 with XML mapper files in `src/main/resources/mapper/`
- **Security:** Apache Shiro 1.4.0 + JWT (jjwt 0.9.0); Redis-backed sessions via shiro-redis
- **Database:** MySQL, no migration tool — schema managed via manual DDL in `DB/`
- **Cache:** Redis (required dependency)

**Domain modules** under `com.geeke.*` — each follows the same layout: `controller/`, `dao/`, `entity/`, `service/`. Key modules: `admin` (RBAC), `outpatient` (registration, medical records, recipes), `stock` (inventory, drugs), `toll` (billing), `cure` (treatment), `member` (patients), `org` (organizations), `schedule` (scheduling), `gen` (code generation).

**Layer conventions:**
- Entities extend `DataEntity<T>` (audit fields, logical delete via `del_flag`, UUID ids via `IdGen.uuid()`)
- DAOs extend `CrudDao<T>` with standard CRUD interface; SQL lives in XML mappers using `Common.whereParams` fragment
- Services extend `CrudService<D, T>` with `@Transactional(readOnly = true)` default
- Controllers extend `BaseController`, return `ResponseEntity<JSONObject>` via `ResultUtil.successJson()`
- Standard REST pattern: `GET /{id}`, `POST /list` (paginated), `POST /save`, `POST /delete`, bulk ops

**Code generation** (`com.geeke.gen`): Freemarker-based engine that introspects DB tables and generates entity/DAO/service/controller/mapper XML + frontend components. Config in `application.yml` under `genConfigure`.

### Frontend (`Code/medical2.0/client/`)

- **Vue 2** (Options API), **Element UI 2.x**, loaded via CDN externals (not bundled)
- **Webpack** via Vue CLI 5 (`vue.config.js`)
- **State:** Vuex (thin — mostly UI state; data fetching is in components)
- **HTTP:** Axios wrapper in `src/utils/request.js` with JWT token injection

**Dynamic routing:** Routes are entirely server-driven. After login, the backend returns authorized menu/route definitions; frontend builds both sidebar menu and Vue Router routes via `router.addRoute()`. Route components resolve to `@/views/<code>/index.vue`.

**View conventions:**
- `views/<module>/index.vue` (list view) + `<module>Form.vue` (edit form)
- List views extend `MainUI` (`views/components/mainUI.vue`) which provides column management, query forms, loading state
- `metadata.js` files in view directories drive data configuration (table IDs, scheme IDs)

**API pattern** (`src/api/<module>/`): Named exports for `listXxxPage` (POST), `getXxxById` (GET), `saveXxx` (POST), `deleteXxx` (POST).

**Auth:** All session state (token, user, routers, permissions, company) stored in `sessionStorage` via `src/utils/auth.js`.

## Configuration

Profiles: `dev` (localhost), `demo` (default in application.yml), `test`, `prod`. Each has its own `application-<profile>..yml`. Server port is 7016 across all profiles.

The frontend dev server proxies `/api` to `http://localhost:7016` (configured in `vue.config.js`).

## Key Directories

```
DB/                          # Database init SQL scripts
Doc/                         # Operation manuals, feature specs (Chinese)
Code/medical2.0/back/        # Spring Boot backend
Code/medical2.0/client/      # Vue 2 frontend
```

## Tech Stack Summary

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 2.1, MyBatis-Plus, Shiro, JWT, Redis |
| Frontend | Vue 2, Element UI, Vuex, Vue Router, Axios |
| Database | MySQL (no migration tool) |
| Build | Maven (backend), Vue CLI/Webpack (frontend) |
| Reporting | UReport2, hiprint |
| ID Generation | Baidu uid-generator (system-scope JAR in `lib/`) |
