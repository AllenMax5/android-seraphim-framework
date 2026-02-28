# Delicacies App — 食堂签到助手 PRD

> **版本**: v1.0.0  
> **最后更新**: 2026-02-28  
> **模块**: `apps/delicacies` · `shareds/delicacies` · `domain/scaffolding-delicacies`  
> **包名**: `com.seraphim.app.yxsg`

---

## 1. 产品概述

### 1.1 产品定位

Delicacies 是一款**轻量级食堂用餐签到应用**，帮助用户记录每日在食堂的午餐与晚餐次数。所有数据完全存储于本地数据库（Room），无需后台服务器，注重隐私与离线可用性。

### 1.2 目标用户

- 在校学生、企业员工等需要在食堂就餐并希望追踪用餐习惯的人群。

### 1.3 核心价值

| 价值点 | 说明 |
|--------|------|
| 简单签到 | 一键完成午餐 / 晚餐签到，无需繁琐操作 |
| 日历可视化 | 按月展示签到状态，一目了然 |
| 用餐统计 | 图表直观呈现当月用餐次数与进度 |
| 智能提醒 | 每日自动推送通知，不遗漏任何一餐 |
| 纯离线 | 数据全部本地存储，保护用户隐私 |

---

## 2. 功能需求

### 2.1 签到功能（P0 — 核心）

#### 2.1.1 签到页面（首页）

- **欢迎标语区域**
  - 顶部展示个性化欢迎语，如 _"今天也要好好吃饭哦 🍚"_。
  - 根据当前时间段自动切换文案（上午 → 午餐提示；下午 → 晚餐提示）。
- **签到操作**
  - 提供 **午餐** 与 **晚餐** 两个独立签到按钮。
  - 签到状态实时反馈：未签到（可点击）→ 已签到（灰色/打勾）。
  - 同一餐次当日仅允许签到一次，重复点击无效。
- **当月统计图表**
  - 在签到按钮下方展示当前月份用餐次数的**环形进度图 / 柱状图**。
  - 显示内容：已签到次数 / 月度上限（20 次）。
  - 颜色区分午餐与晚餐占比。
- **签到规则**
  - 每天最多签到 **2 次**（午餐 1 次 + 晚餐 1 次）。
  - 每月累计上限 **20 次**。
  - 达到上限后签到按钮禁用，并提示 _"本月签到已达上限"_。

#### 2.1.2 签到数据模型

```
CheckInRecord {
    id: Long (PK, auto-generate)
    date: LocalDate           // 签到日期
    mealType: MealType        // LUNCH | DINNER
    checkedIn: Boolean        // 是否签到
    createdAt: LocalDateTime  // 创建时间
    updatedAt: LocalDateTime  // 最后修改时间
}
```

```kotlin
enum class MealType {
    LUNCH,   // 午餐
    DINNER   // 晚餐
}
```

### 2.2 日历页面（P0 — 核心）

#### 2.2.1 当前月份日历视图

- 以**月视图**形式展示，每日格子内标示签到状态：
  - ⬜ 无签到
  - 🟡 签到 1 次（仅午餐或仅晚餐）
  - 🟢 签到 2 次（午餐 + 晚餐）
- 点击某一天可进入**日签到详情弹窗**，支持：
  - 查看该日午餐 / 晚餐签到状态。
  - **修改**签到状态（补签 / 取消签到）。
  - 修改后实时刷新日历视图与统计图表。

#### 2.2.2 历史月份查询

- 支持左右滑动或点击箭头切换月份。
- 可查看任意历史月份的签到记录。
- 历史数据只读展示（默认），可选是否允许修改历史记录。

#### 2.2.3 日历页面头部

- 显示当前查看月份（如 **2026 年 2 月**）。
- 显示当月签到汇总：已签到 X 次 / 20 次。

### 2.3 通知提醒（P1 — 重要）

#### 2.3.1 每日自动推送

- 使用 **Jetpack WorkManager** 实现定时通知：
  - **午餐提醒**：每日 11:30 推送 → _"午餐时间到了，别忘了签到哦！"_
  - **晚餐提醒**：每日 17:30 推送 → _"晚餐时间到了，记得签到！"_
- Worker 使用 `PeriodicWorkRequest`，间隔为 24 小时，设置 `initialDelay` 对齐目标时间。
- 使用 `@HiltWorker` 注入依赖进行 DI 集成。

#### 2.3.2 通知配置

- 通知渠道：`delicacies_meal_reminder`
- 用户可在设置页关闭提醒功能（实质为取消 WorkManager 任务）。

### 2.4 设置页面（P2 — 增强）

| 设置项 | 说明 |
|--------|------|
| 通知开关 | 开启/关闭每日签到提醒 |
| 午餐提醒时间 | 自定义午餐通知时间（默认 11:30） |
| 晚餐提醒时间 | 自定义晚餐通知时间（默认 17:30） |
| 月度上限调整 | 修改每月最大签到次数（默认 20 次） |
| 欢迎标语自定义 | 用户自行编辑首页欢迎语 |
| 数据导出 | 导出签到记录为 CSV 文件 |
| 清除数据 | 一键清空所有签到记录 |

---

## 3. 非功能需求

### 3.1 性能

- 日历视图月份切换响应时间 < 100ms。
- Room 数据库查询使用 `Flow<List<T>>` 保证响应式更新。
- LazyColumn / LazyGrid 列表使用 `key` 避免不必要的 recomposition。

### 3.2 可靠性

- WorkManager 任务需设置 `ExistingPeriodicWorkPolicy.KEEP`，避免重复注册。
- 数据库操作使用 `@Transaction` 确保一致性。

### 3.3 兼容性

- **最低 SDK**: API 26 (Android 8.0)
- **目标 SDK**: API 35
- **支持设备**: 手机 & 平板

### 3.4 隐私与安全

- 无网络请求，无数据上传。
- 所有数据存储于应用内部 Room 数据库。
- 卸载应用即彻底清除所有数据。

---

## 4. 技术架构

### 4.1 技术栈

| 层级 | 技术选型 |
|------|----------|
| UI | Jetpack Compose + Material 3 |
| 导航 | Navigation Compose |
| 状态管理 | ViewModel + StateFlow |
| 依赖注入 | Hilt |
| 本地存储 | Room Database |
| 后台任务 | Jetpack WorkManager (`CoroutineWorker`) |
| 通知 | NotificationManager + NotificationChannel |
| 图表 | Compose Canvas 自绘 / Vico Charts |
| 日历 | 自定义 Compose 日历组件 |

### 4.2 模块结构

```
apps/delicacies/                    ← App 壳模块（MainActivity、Application、DI Setup）
├── com.seraphim.app.yxsg
│   ├── MainActivity.kt
│   ├── YssgApplication.kt
│   ├── navigation/                 ← 导航图定义
│   ├── ui/
│   │   ├── checkin/                ← 签到首页
│   │   ├── calendar/              ← 日历页面
│   │   └── settings/              ← 设置页面
│   └── worker/                    ← WorkManager Worker

shareds/delicacies/                 ← 跨平台共享模块
├── commonMain/
│   ├── data/
│   │   ├── db/                    ← Room Entity、DAO、Database
│   │   └── repository/            ← Repository 实现
│   ├── domain/
│   │   ├── model/                 ← 领域模型 (CheckInRecord, MealType)
│   │   └── usecase/               ← 用例（签到、查询统计等）
│   └── di/                        ← DI 模块

domain/scaffolding-delicacies/      ← 领域脚手架
```

### 4.3 数据库设计

#### `check_in_records` 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | INTEGER | PK, AUTO INCREMENT | 主键 |
| `date` | TEXT | NOT NULL | 签到日期 (yyyy-MM-dd) |
| `meal_type` | TEXT | NOT NULL | 餐次 (LUNCH / DINNER) |
| `checked_in` | INTEGER | NOT NULL, DEFAULT 1 | 是否签到 |
| `created_at` | TEXT | NOT NULL | 创建时间 |
| `updated_at` | TEXT | NOT NULL | 最后修改时间 |

**唯一约束**: `UNIQUE(date, meal_type)` — 同一天同一餐次仅允许一条记录。

#### 核心 DAO 接口

```kotlin
@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_in_records WHERE date BETWEEN :startDate AND :endDate")
    fun getRecordsByDateRange(startDate: String, endDate: String): Flow<List<CheckInEntity>>

    @Query("SELECT COUNT(*) FROM check_in_records WHERE date LIKE :monthPrefix AND checked_in = 1")
    fun getMonthlyCheckInCount(monthPrefix: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: CheckInEntity)

    @Query("UPDATE check_in_records SET checked_in = :status, updated_at = :updatedAt WHERE date = :date AND meal_type = :mealType")
    suspend fun updateCheckInStatus(date: String, mealType: String, status: Boolean, updatedAt: String)
}
```

### 4.4 核心 Use Cases

| Use Case | 输入 | 输出 | 说明 |
|----------|------|------|------|
| `CheckInUseCase` | date, mealType | Result<Unit> | 执行签到，校验月度上限 |
| `GetMonthlyRecordsUseCase` | yearMonth | Flow<List<CheckInRecord>> | 获取指定月份所有签到记录 |
| `GetMonthlyStatsUseCase` | yearMonth | Flow<MonthlyStats> | 获取月度统计（总次数、午/晚餐分布） |
| `UpdateCheckInUseCase` | date, mealType, status | Result<Unit> | 修改签到状态（补签/取消） |
| `CanCheckInUseCase` | yearMonth | Flow<Boolean> | 判断当月是否还能签到 |

---

## 5. 页面交互设计

### 5.1 签到首页

```
┌──────────────────────────────────┐
│  🍚 今天也要好好吃饭哦            │  ← 欢迎标语
├──────────────────────────────────┤
│                                  │
│    ┌──────────┐ ┌──────────┐     │
│    │  🥗 午餐  │ │  🍜 晚餐  │     │  ← 签到按钮
│    │  已签到 ✓ │ │  点击签到  │     │
│    └──────────┘ └──────────┘     │
│                                  │
├──────────────────────────────────┤
│        本月用餐统计               │
│   ┌────────────────────┐         │
│   │    ██████░░░░       │         │  ← 环形进度图
│   │    12 / 20 次       │         │
│   │  午餐: 7  晚餐: 5   │         │
│   └────────────────────┘         │
│                                  │
├──────────────────────────────────┤
│  📅 日历  │  ⚙️ 设置              │  ← 底部导航
└──────────────────────────────────┘
```

### 5.2 日历页面

```
┌──────────────────────────────────┐
│  ◀  2026 年 2 月  ▶    12/20 次  │  ← 月份切换 + 汇总
├──────────────────────────────────┤
│  日   一   二   三   四   五   六  │
│  ─────────────────────────────── │
│       1    2    3    4    5    6  │
│       ⬜   🟢   🟡   ⬜   🟢   ⬜  │
│  7    8    9    10   11   12   13 │
│  🟡   🟢   ⬜   🟢   🟡   🟢   ⬜  │
│  ...                             │
├──────────────────────────────────┤
│  点击日期查看/修改签到详情          │
└──────────────────────────────────┘
```

### 5.3 日签到详情弹窗

```
┌─────────────────────────┐
│     2026 年 2 月 15 日    │
├─────────────────────────┤
│  🥗 午餐    [✓ 已签到]   │  ← 可切换
│  🍜 晚餐    [  未签到]   │  ← 可切换
├─────────────────────────┤
│   [ 取消 ]    [ 保存 ]   │
└─────────────────────────┘
```

---

## 6. 导航结构

```
NavHost
├── CheckInScreen        (route: "checkin")        ← 默认首页
├── CalendarScreen       (route: "calendar")
│   └── DayDetailDialog  (route: "calendar/{date}")
└── SettingsScreen       (route: "settings")
```

使用 **Bottom Navigation Bar** 在三个主页面间切换。

---

## 7. WorkManager 定时通知方案

### 7.1 Worker 实现

```kotlin
@HiltWorker
class MealReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val mealType = inputData.getString(KEY_MEAL_TYPE) ?: return Result.failure()
        notificationHelper.showMealReminder(MealType.valueOf(mealType))
        return Result.success()
    }
}
```

### 7.2 注册策略

```kotlin
fun scheduleMealReminders(workManager: WorkManager) {
    // 午餐提醒 — 每日 11:30
    val lunchRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(calculateDelay(11, 30), TimeUnit.MILLISECONDS)
        .setInputData(workDataOf(KEY_MEAL_TYPE to MealType.LUNCH.name))
        .build()

    // 晚餐提醒 — 每日 17:30
    val dinnerRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(calculateDelay(17, 30), TimeUnit.MILLISECONDS)
        .setInputData(workDataOf(KEY_MEAL_TYPE to MealType.DINNER.name))
        .build()

    workManager.enqueueUniquePeriodicWork("lunch_reminder", KEEP, lunchRequest)
    workManager.enqueueUniquePeriodicWork("dinner_reminder", KEEP, dinnerRequest)
}
```

---

## 8. 里程碑 & 排期

| 阶段 | 内容 | 预估工时 |
|------|------|----------|
| M1 — 基础框架 | Room 数据库搭建、Entity/DAO、Repository | 2 天 |
| M2 — 签到首页 | 签到 UI、ViewModel、欢迎标语、签到逻辑 | 2 天 |
| M3 — 统计图表 | 环形进度图 / 柱状图、月度统计数据 | 1.5 天 |
| M4 — 日历页面 | 自定义月视图日历、日期签到弹窗、月份切换 | 3 天 |
| M5 — 通知系统 | WorkManager Worker、通知渠道、定时推送 | 1.5 天 |
| M6 — 设置页面 | 通知开关、时间配置、数据导出/清除 | 1 天 |
| M7 — 测试优化 | 单元测试、UI 测试、性能调优 | 2 天 |

**总计**: ~13 天

---

## 9. 验收标准

- [ ] 用户可在首页完成午餐/晚餐签到，每餐每日仅一次。
- [ ] 每月累计签到不超过 20 次，达上限后签到按钮禁用。
- [ ] 日历页面正确展示当月每日签到状态（无/单次/双次）。
- [ ] 点击日历日期可查看并修改该日签到状态。
- [ ] 支持切换至历史月份查看签到记录。
- [ ] 每日 11:30 和 17:30 自动推送签到提醒通知。
- [ ] 应用卸载后不残留任何用户数据。
- [ ] 首页统计图表准确反映当月签到进度。
- [ ] 所有数据库操作在非主线程执行，UI 流畅无卡顿。

---

## 10. 附录

### 10.1 参考资源

- [Material 3 Design for Compose](https://m3.material.io/develop/android/jetpack-compose)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [WorkManager Guide](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)

### 10.2 名词解释

| 术语 | 说明 |
|------|------|
| 签到 | 用户在食堂就餐后在 App 上确认记录的操作 |
| 餐次 | 午餐（LUNCH）或晚餐（DINNER） |
| 月度上限 | 每月最多可签到次数，默认 20 次 |
| 补签 | 对过去日期进行签到操作 |
