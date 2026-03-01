# Seraphim Framework

> 基于 **Kotlin Multiplatform (KMP)** 的多应用 Android 开发框架，采用模块化架构与约定插件驱动的构建系统。

---

## 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [项目架构](#项目架构)
- [模块总览](#模块总览)
- [应用模块](#应用模块)
- [核心模块](#核心模块)
- [构建系统](#构建系统)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [代码规范](#代码规范)
- [许可证](#许可证)

---

## 项目简介

**Seraphim Framework** 是一个多模块、多应用的 Android 开发平台，基于 Kotlin Multiplatform
构建，支持同时承载多个独立 App。每个 App 拥有独立的业务逻辑（shared 层），共享底层核心能力（网络、存储、权限、工具类），并通过统一的
**Convention Plugin** 约定插件体系管理构建配置，消除模块间的样板代码。

### 当前已有应用

| 应用                     | 描述                                           | 包名                     |
|------------------------|----------------------------------------------|------------------------|
| **Delicacies（食堂签到助手）** | 轻量级食堂用餐签到 App，支持日历可视化、统计图表、智能提醒，纯离线运行        | `com.seraphim.yxsg`    |
| **Pokémon（宝可梦图鉴）**     | 全方位宝可梦百科与工具类 App，基于 PokeAPI，支持图鉴浏览、属性查询、收藏管理 | `com.seraphim.pokemon` |

---

## 技术栈

| 类别             | 技术选型                                      | 版本             |
|----------------|-------------------------------------------|----------------|
| **语言**         | Kotlin (Multiplatform)                    | 2.3.10         |
| **Android 构建** | AGP (Android Gradle Plugin)               | 9.0.1          |
| **UI 框架**      | Jetpack Compose + Material3               | BOM 2025.12.01 |
| **依赖注入**       | Koin (KSP 注解处理)                           | 4.1.1          |
| **网络请求**       | Ktor (多平台 HTTP 客户端)                       | 3.4.0          |
| **本地数据库**      | Room (KMP, Bundled SQLite)                | 2.8.4          |
| **KV 存储**      | MMKV-Kotlin                               | 1.3.1          |
| **序列化**        | Kotlinx Serialization                     | 1.10.0         |
| **协程**         | Kotlinx Coroutines                        | 1.10.2         |
| **图片加载**       | Coil                                      | 2.7.0          |
| **导航**         | Jetpack Navigation + Compose Destinations | 2.9.6          |
| **日志**         | Napier (多平台) + Logback                    | 2.7.1          |
| **API 代码生成**   | OpenAPI Generator                         | 7.14.0         |
| **代码格式化**      | Spotless + ktlint                         | 1.4.0          |
| **测试**         | JUnit4, Robolectric, Roborazzi (截图测试)     | —              |
| **覆盖率**        | Jacoco                                    | 0.8.12         |
| **Firebase**   | Analytics, Crashlytics, Performance       | BOM 34.7.0     |
| **构建工具**       | Gradle (Kotlin DSL)                       | JDK 21         |

---

## 项目架构

```
┌─────────────────────────────────────────────────────────┐
│                      App Layer                          │
│  ┌──────────────────┐      ┌──────────────────┐         │
│  │  apps/delicacies  │      │   apps/pokemon   │   ...   │
│  │  (Android App)    │      │  (Android App)   │         │
│  └────────┬─────────┘      └────────┬─────────┘         │
├───────────┼─────────────────────────┼───────────────────┤
│           │    Shared Business Layer │                   │
│  ┌────────▼─────────┐      ┌────────▼─────────┐         │
│  │shareds/delicacies │      │ shareds/pokemon  │   ...   │
│  │  (KMP Library)    │      │  (KMP Library)   │         │
│  └──┬────┬────┬─────┘      └──┬────┬────┬─────┘         │
├─────┼────┼────┼───────────────┼────┼────┼───────────────┤
│     │    │    │   Core Layer  │    │    │                │
│  ┌──▼──┐ │ ┌──▼────┐      ┌──▼──┐ │ ┌──▼──────────┐    │
│  │ net │ │ │storage│      │ net │ │ │ permissions │    │
│  │work │ │ │      │       │work │ │ │ (Android)  │    │
│  └─────┘ │ └──────┘       └─────┘ │ └────────────┘    │
│       ┌──▼──┐                  ┌──▼──┐                  │
│       │utils│                  │utils│                  │
│       └─────┘                  └─────┘                  │
├─────────────────────────────────────────────────────────┤
│                    Build Logic                          │
│          build-logic/convention (19 plugins)            │
└─────────────────────────────────────────────────────────┘
```

### 模块依赖关系

```
apps/delicacies ──► shareds/delicacies ──► core/network ──► utils
                                       ──► core/storage
                 ──► core/permissions
                 ──► utils

apps/pokemon   ──► shareds/pokemon    ──► core/network ──► utils
                                      ──► core/storage
                ──► utils
```

### 分层说明

| 层级               | 职责                                                       | 平台                  |
|------------------|----------------------------------------------------------|---------------------|
| **App Layer**    | UI（Compose）、导航、ViewModel、DI 组装、平台入口                      | Android             |
| **Shared Layer** | 业务逻辑（UseCase）、数据仓库（Repository）、数据库（Room DAO/Entity）、网络服务 | KMP (Android + iOS) |
| **Core Layer**   | 基础设施：HTTP 客户端封装、数据库工厂、KV 存储、权限管理                         | KMP / Android       |
| **Utils**        | 通用工具：日志、类型转换、单位换算                                        | KMP                 |
| **Build Logic**  | 约定插件：统一 compileSdk、minSdk、Compose、Room、DI、Lint 等配置       | Gradle              |

---

## 模块总览

```
android-seraphim-framework/
├── apps/                          # 应用模块（Android Application）
│   ├── delicacies/                #   食堂签到助手
│   ├── pokemon/                   #   宝可梦图鉴
│   ├── music/                     #   (预留)
│   └── nfc/                       #   (预留)
├── shareds/                       # 共享业务模块（KMP Library）
│   ├── delicacies/                #   签到业务逻辑 + 数据层
│   ├── pokemon/                   #   宝可梦业务逻辑 + 数据层
│   ├── music/                     #   (预留)
│   └── nfc/                       #   (预留)
├── core/                          # 核心基础模块
│   ├── network/                   #   KMP 网络封装（Ktor）
│   ├── storage/                   #   KMP 存储封装（Room + MMKV）
│   └── permissions/               #   Android 权限管理
├── utils/                         # KMP 通用工具库
├── build-logic/                   # Gradle 约定插件
│   └── convention/                #   19 个自定义插件
├── doc/                           # 产品文档
│   ├── delicacies-app-prd.md      #   Delicacies PRD
│   └── pokemon-app-prd.md         #   Pokémon PRD
├── gradle/                        # Gradle 配置
│   ├── libs.versions.toml         #   版本目录
│   └── init.gradle.kts            #   Spotless 代码格式化
└── manifests/                     # Google repo 多仓管理清单
    └── default.xml
```

---

## 应用模块

### Delicacies — 食堂签到助手

轻量级食堂用餐签到应用，帮助用户记录每日午餐与晚餐，所有数据完全本地存储。

**核心功能：**

- ✅ 一键午餐 / 晚餐签到（每日最多 2 次，每月上限 20 次）
- 📅 月度日历视图，直观展示签到状态
- 📊 环形统计图表，实时呈现当月用餐进度
- 🔔 WorkManager 定时提醒，不遗漏每一餐
- 📝 历史记录查询与补签支持

**关键技术：** Room 本地数据库 · Compose Destinations 导航 · WorkManager 定时任务 · Calendar 组件

### Pokémon — 宝可梦图鉴

全方位宝可梦百科应用，基于 PokeAPI v2，提供图鉴浏览、属性查询、进化链查看等功能。

**核心功能：**

- 📖 全世代 1000+ 宝可梦无限滚动图鉴列表
- 🔍 详情页：种族值、精灵图、属性标签、进化链
- ⚔️ 属性克制速查
- ⭐ 收藏管理与本地图鉴
- 🌐 多语言名称支持（中 / 英 / 日）

**关键技术：** Ktor HTTP 客户端 · Room 离线缓存 · Paging 3 分页加载 · Coil 图片加载

---

## 核心模块

### core/network

KMP 网络层封装，提供类型安全的 HTTP 请求处理。

- `BffResult<T>` — 密封类统一请求结果（Success / Failure），包含
  HttpError、SerializationError、NetworkError 等错误类型
- `receiveBffResult<DTO>()` — 类型安全的 Ktor 响应解析扩展函数
- 预配置 `kotlinx.serialization.json.Json` 实例（宽松模式、忽略未知字段）
- 平台引擎：Android → OkHttp，iOS → Darwin

### core/storage

KMP 存储层封装，统一管理持久化方案。

- **Room 数据库工厂** — `DatabaseFactory` 接口 + 平台实现（Android / Native），通过 `DatabaseBuilder`
  统一构建
- **MMKV KV 存储** — `safeKvSave()` / `safeKvGet()` / `kvRemove()` 等安全封装

### core/permissions

Android 权限管理工具（纯 Android 模块），基于 AndroidX 封装权限请求流程。

### utils

KMP 通用工具库：

- `Logger` — 基于 Napier 的多平台日志工具
- `TypeUtils` — 类型转换工具
- 单位换算工具集 — 距离、速度、温度、气压、油耗等

---

## 构建系统

### Convention Plugins（约定插件）

项目通过 `build-logic/convention` 模块定义了 **19 个自定义 Gradle 插件**，统一管理所有模块的构建配置：

| 插件 ID                                   | 用途                                                        |
|-----------------------------------------|-----------------------------------------------------------|
| `seraphim.android.application`          | Android Application 基础配置（compileSdk、minSdk、Lint、APK 签名验证） |
| `seraphim.android.application.compose`  | 为 Application 模块启用 Jetpack Compose                        |
| `seraphim.android.application.firebase` | 集成 Firebase Analytics / Crashlytics / Performance         |
| `seraphim.android.application.flavors`  | 配置 demo / prod 产品变体                                       |
| `seraphim.android.application.jacoco`   | Application 模块代码覆盖率                                       |
| `seraphim.android.library`              | Android Library 基础配置                                      |
| `seraphim.android.library.compose`      | 为 Library 模块启用 Jetpack Compose                            |
| `seraphim.android.library.jacoco`       | Library 模块代码覆盖率                                           |
| `seraphim.android.feature`              | Feature 模块（自动引入 Hilt + 序列化 + 导航）                          |
| `seraphim.android.room`                 | Room 数据库配置（KSP + 自动迁移）                                    |
| `seraphim.android.lint`                 | Lint 检查配置（XML + SARIF 报告）                                 |
| `seraphim.android.test`                 | 测试模块配置                                                    |
| `seraphim.hilt`                         | Hilt 依赖注入（支持 JVM 与 Android）                               |
| `seraphim.koin`                         | Koin 依赖注入（支持 KSP 注解处理）                                    |
| `seraphim.jvm.library`                  | 纯 JVM/Kotlin 库                                            |
| `seraphim.kotlin.multiplatform.library` | KMP 库配置（Android + iOS x64/arm64/simulatorArm64）           |
| `seraphim.openapi.generator`            | OpenAPI 代码生成（Kotlin 多平台客户端）                               |
| `seraphim.spring.boot`                  | Spring Boot 服务端配置                                         |

### SDK 版本

| 配置项                 | 值  |
|---------------------|----|
| compileSdk          | 36 |
| targetSdk           | 36 |
| minSdk              | 28 |
| JVM Target (App)    | 11 |
| JVM Toolchain (KMP) | 21 |

### KMP 目标平台

| 平台      | Target                                          |
|---------|-------------------------------------------------|
| Android | via `androidTarget()`                           |
| iOS     | `iosX64()`, `iosArm64()`, `iosSimulatorArm64()` |

### 多仓管理

项目支持通过 Google `repo` 工具进行多仓管理（`manifests/default.xml`），可将 `build-logic`、`gradle`、
`utils`、`core/permissions` 等模块拆分为独立 Git 仓库。

---

## 环境要求

| 依赖                 | 最低版本                                         |
|--------------------|----------------------------------------------|
| **JDK**            | 17+（推荐 21，Gradle Daemon 使用 JetBrains JDK 21） |
| **Android Studio** | Meerkat 或更新版本（需支持 AGP 9.0）                   |
| **Gradle**         | 使用项目自带 Wrapper（`gradlew`）                    |
| **Android SDK**    | API 36 (compileSdk)                          |

---

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/li-lance/android-seraphim-framework.git
cd android-seraphim-framework
```

### 2. 构建指定应用

```bash
# 构建 Delicacies App (Debug)
./gradlew :apps:delicacies:assembleDebug

# 构建 Pokémon App (Debug)
./gradlew :apps:pokemon:assembleDebug
```

### 3. 运行应用

在 Android Studio 中选择对应的 app 模块运行配置，或使用命令行：

```bash
./gradlew :apps:delicacies:installDebug
./gradlew :apps:pokemon:installDebug
```

### 4. 运行测试

```bash
# 单元测试
./gradlew test

# 特定模块测试
./gradlew :shareds:delicacies:test
./gradlew :shareds:pokemon:test
```

### 5. 代码格式化

```bash
# 检查格式
./gradlew spotlessCheck

# 自动修复
./gradlew spotlessApply
```

### 6. 生成模块依赖图

```bash
./gradlew generateModulesGraphvizText
```

---

## 新增应用指南

1. 在 `apps/` 下创建新模块目录
2. 在 `shareds/` 下创建对应的共享业务模块
3. 在 `settings.gradle.kts` 中注册新模块
4. 在 app 模块的 `build.gradle.kts` 中应用约定插件：
   ```kotlin
   plugins {
       alias(libs.plugins.seraphim.android.application)
       alias(libs.plugins.seraphim.android.application.compose)
       alias(libs.plugins.seraphim.koin)
       alias(libs.plugins.kotlin.serialization)
   }
   ```
5. 在 shared 模块中实现业务逻辑（Repository、UseCase、DAO）
6. 在 app 模块中构建 UI 层（Screen、ViewModel、Navigation）

---

## 代码规范

- **格式化**：Spotless + ktlint（Android 风格），通过 `gradle/init.gradle.kts` 全局启用
- **Lint**：通过 `seraphim.android.lint` 插件统一配置，输出 XML + SARIF 报告
- **许可头**：所有 Kotlin 文件需包含 Apache 2.0 许可头（由 Spotless 自动检查）
- **架构约束**：
    - App 层仅处理 UI 与导航，不直接访问数据层
    - Shared 层采用 Clean Architecture（UseCase → Repository → DataSource）
    - Core 层提供平台无关的基础设施抽象

---

## 许可证

```
Copyright 2024 The Seraphim Framework Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
