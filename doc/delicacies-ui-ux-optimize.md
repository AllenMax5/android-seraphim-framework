# Delicacies App — UI/UX 优化方案 (Material Design 3)

> **版本**: v1.0  
> **日期**: 2026-04-21  
> **目标**: 基于 Material Design 3 规范，全面优化现有 UI/UX  
> **范围**: `apps/delicacies` 全部界面

---

## 1. 现状诊断

### 1.1 已有基础（做得好的）

| 项            | 说明                                              |
|--------------|-------------------------------------------------|
| Theme 结构     | 已定义 Light/Dark ColorScheme，支持 Dynamic Color     |
| Edge-to-Edge | MainActivity 已启用 `enableEdgeToEdge()`           |
| 底部导航         | 使用 `NavigationBar` + `NavigationBarItem`，符合 MD3 |
| 基础组件         | 已使用 Card、Text、Icon、Switch 等 MD3 组件              |

### 1.2 主要问题清单

#### A. Design System 不完整

- **缺少 Typography 定义** — 使用默认 Material 3 字体排布，中文字号行高未调优
- **Color Scheme 不完整** — 缺少 `outline`、`surfaceTint`、`inversePrimary` 等语义色
- **缺少 Shape 系统** — 全部使用默认 `MaterialTheme.shapes`
- **缺少 Spacing Tokens** — padding/margin 数值硬编码，页面间不统一

#### B. 组件层面

- **MealCheckInCard** — 使用 `Card` 做按钮，语义不准确；缺少按压动效和 haptic feedback
- **DonutChart** — 无障碍缺失；图例用 Canvas 绘圆点过于复杂
- **DayDetailDialog** — 使用旧式 `AlertDialog`，无法完全自定义样式
- **CalendarScreen** — Card 嵌套过多，视觉层级混乱；星期标题与日历未分离
- **SettingsRow** — 可替换为 MD3 `ListItem`，更符合规范

#### C. 布局与间距

- 各页面水平 padding 不统一（CheckIn 用 20dp，Calendar/Settings 用 16dp）
- `Scaffold` 的 `paddingValues` 直接传给 `NavHost`，未针对 edge-to-edge 调整

#### D. 动效与交互

- **NavHost 无页面切换动画** — 切页生硬
- **签到缺少触觉反馈** — 成功签到时无 haptic 确认
- **日历月份切换无手势** — 只能通过按钮切换
- **缺少 Pull-to-Refresh** — 首页数据无法手动刷新

#### E. 无障碍

- 多处 `Icon` 的 `contentDescription = null`
- 日历日期格子在小屏幕上触摸目标可能小于 48dp

#### F. 视觉层级

- 所有 Card 使用同色 surface，未通过 `tonalElevation` 区分层级深度
- 今日日期高亮使用 `primaryContainer`，与按钮状态色冲突

---

## 2. 优化方案

### 2.1 Design System 层

#### 2.1.1 Typography 系统

创建 `Type.kt`，定义符合中文阅读的字体排布：

```kotlin
// Material 3 默认 Type Scale + 中文调优
val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(lineHeight = 64.sp),
    displayMedium = baseline.displayMedium.copy(lineHeight = 52.sp),
    displaySmall = baseline.displaySmall.copy(lineHeight = 44.sp),
    headlineLarge = baseline.headlineLarge.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
    ),
    titleLarge = baseline.titleLarge.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
    ),
    titleMedium = baseline.titleMedium.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    ),
    titleSmall = baseline.titleSmall.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = baseline.bodyLarge.copy(lineHeight = 24.sp),
    bodyMedium = baseline.bodyMedium.copy(lineHeight = 20.sp),
    bodySmall = baseline.bodySmall.copy(lineHeight = 16.sp),
    labelLarge = baseline.labelLarge.copy(fontWeight = FontWeight.Medium),
    labelMedium = baseline.labelMedium.copy(letterSpacing = 0.5.sp),
    labelSmall = baseline.labelSmall.copy(letterSpacing = 0.5.sp),
)
```

**关键点**:

- 中文标题加粗（Medium/SemiBold/Bold），避免默认字重过细
- 增大行高（中文需要比英文更大的行间距）
- 标签文字增加字间距，提升可读性

#### 2.1.2 Color Scheme 补全

补全 `Color.kt` 和 `Theme.kt` 中缺失的语义色：

```kotlin
// 新增语义色
val Error80 = Color(0xFFFFB4AB)
val Error40 = Color(0xFFBA1A1A)
val Outline = Color(0xFF757575)
val OutlineVariant = Color(0xFFBDBDBD)
val Scrim = Color(0xFF000000)
val InversePrimary = Color(0xFF99F2B3)
val SurfaceTint = Green40
```

```kotlin
// LightColorScheme 补全
lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green80,
    onPrimaryContainer = Green20,
    inversePrimary = InversePrimary,
    secondary = Orange40,
    onSecondary = Color.White,
    secondaryContainer = Orange80,
    onSecondaryContainer = Orange30,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue80,
    onTertiaryContainer = Color.Black,
    error = Error40,
    onError = Color.White,
    errorContainer = Error80,
    onErrorContainer = Color.Black,
    outline = Outline,
    outlineVariant = OutlineVariant,
    scrim = Scrim,
    surfaceTint = SurfaceTint,
    // ... 现有颜色保持不变
)
```

#### 2.1.3 Shape 系统

```kotlin
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
```

**应用策略**:

- 签到按钮 Card → `extraLarge`（28dp，更圆润亲和）
- 统计图表 Card → `large`（16dp）
- 底部 Sheet / Dialog → `extraLarge`（顶部圆角）

#### 2.1.4 Spacing Tokens

```kotlin
object Spacing {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
}

// 页面统一边距
val PageHorizontalPadding = 16.dp
val PageVerticalPadding = 16.dp
```

---

### 2.2 页面级优化

#### 2.2.1 签到首页 (CheckInScreen)

**当前问题**:

- 欢迎标语 Card 和统计 Card 形状相同，层级无法区分
- 签到按钮用 Card，语义不准确，按压反馈弱
- 签到成功无任何感官反馈

**优化方案**:

```
优化前: Card (按钮) + Card (统计) — 平级
优化后: Surface (欢迎) + FilledTonalButton (签到) + ElevatedCard (统计)
        通过 tonalElevation 区分层级
```

| 元素      | 优化前            | 优化后                                         | 说明              |
|---------|----------------|---------------------------------------------|-----------------|
| 欢迎标语    | Card           | Surface (tonalElevation=1.dp)               | 降低层级，不作为主要操作区   |
| 午餐/晚餐按钮 | Card + onClick | `FilledTonalButton` (大尺寸)                   | 语义准确，MD3 按钮自带动效 |
| 统计图表    | Card           | `ElevatedCard` (elevation=2.dp)             | 提升层级，突出数据       |
| 上限提示    | Text           | `AssistChip` (error 色)                      | 更醒目的标签形态        |
| 签到成功反馈  | 无              | `hapticFeedback.performHapticFeedback(...)` | 触觉确认            |

**签到按钮状态设计**:

```kotlin
@Composable
fun MealCheckInButton(
    mealType: MealType,
    checkedIn: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    val icon = when {
        checkedIn -> Icons.Rounded.CheckCircle
        mealType == MealType.LUNCH -> Icons.Rounded.LunchDining
        else -> Icons.Rounded.DinnerDining
    }

    val label = when {
        checkedIn -> "${mealType.label} · 已签到"
        !enabled -> "本月已达上限"
        else -> "点击签到${mealType.label}"
    }

    FilledTonalButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            onClick()
        },
        enabled = enabled && !checkedIn,
        modifier = modifier.height(120.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (checkedIn) {
                mealType.color.copy(alpha = 0.12f)
            } else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (checkedIn) mealType.color else MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
```

**DonutChart 优化**:

```kotlin
// 1. 无障碍支持
Box(
    modifier = Modifier.semantics {
        contentDescription = "本月已签到 ${stats.totalCount} 次，目标 ${stats.maxCount} 次，" +
            "其中午餐 ${stats.lunchCount} 次，晚餐 ${stats.dinnerCount} 次"
    }
)

// 2. 图例改为 Box + Icon，不用 Canvas
Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
    // ...
}
```

#### 2.2.2 日历页面 (CalendarScreen)

**当前问题**:

- 月份标题、星期标题、日历、图例 全部各自包 Card，视觉臃肿
- 日期格子只有小圆点表示签到状态，信息密度低
- 无滑动手势切换月份
- 今日高亮色与按钮容器色冲突

**优化方案**:

```
┌──────────────────────────────────┐
│  ◀  2026年2月  ▶        12/20次   │  ← Surface ( tonalElevation=0 )
├──────────────────────────────────┤
│  日   一   二   三   四   五   六  │  ← 星期标题：labelSmall + onSurfaceVariant
│  ═══════════════════════════════  │     底部加 Divider 分隔
│       1    2    3    4    5    6  │
│       ○    ●    ◐    ○    ●    ○  │  ← 日期状态：
│  7    8    9   10   11   12   13  │     ○ 未签到  ◐ 一次  ● 两次
│  ◐    ●    ○    ●    ◐    ●    ○  │     今日用 边框(Outline) 标记，不用填充
│  ...                             │
├──────────────────────────────────┤
│  [图例]  ○未签到  ◐一次  ●两次     │  ← 使用 AssistChip 或小型指示器
└──────────────────────────────────┘
```

| 改动        | 说明                                         |
|-----------|--------------------------------------------|
| 去除多余 Card | 月份标题和日历区域共用一块 Surface                      |
| 星期标题      | 加 Divider 与日期区隔开，使用 `labelSmall`           |
| 日期状态      | 用圆环（outline）表示 1 次，实心圆表示 2 次，更直观           |
| 今日标记      | 改用 `outline` 色边框圆圈，避免与 primaryContainer 冲突 |
| 月份切换      | 增加 `pointerInput` 滑动手势，左右滑动切月              |
| 选中日期      | 点击后日期背景使用 `secondaryContainer`             |

**日期状态视觉设计**:

```kotlin
@Composable
fun DayStatusIndicator(count: Int, modifier: Modifier = Modifier) {
    when (count) {
        0 -> { /* 不显示 */ }
        1 -> Box(
            modifier = modifier
                .size(6.dp)
                .border(2.dp, LunchColor, CircleShape)
        )
        2 -> Box(
            modifier = modifier
                .size(6.dp)
                .background(CheckedInColor, CircleShape)
        )
    }
}
```

#### 2.2.3 日签到详情弹窗 (DayDetailDialog)

**当前问题**:

- 使用 `AlertDialog`，底部按钮强制居中，样式不可控
- Checkbox 在 Dialog 中不是最优交互

**优化方案**: 改用 `ModalBottomSheet` 或自定义 `BasicAlertDialog`

```
底部弹窗方案（推荐）:
┌──────────────────────────────────┐
│         ─── 拖拽条 ───            │  ← BottomSheet 顶部指示器
├──────────────────────────────────┤
│      2026年2月15日  星期五        │  ← titleLarge
├──────────────────────────────────┤
│  ┌────────────────────────────┐  │
│  │  🥗 午餐        [开关]      │  │  ← Switch ListItem
│  │  未签到                     │  │
│  ├────────────────────────────┤  │
│  │  🍜 晚餐        [开关]      │  │
│  │  已签到 · 17:30            │  │
│  └────────────────────────────┘  │  ← ElevatedCard 包裹
│                                  │
│       [    保存修改    ]         │  ← FilledButton，底部固定
└──────────────────────────────────┘
```

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailBottomSheet(
    date: LocalDate,
    dayStatus: DayCheckInStatus?,
    onDismiss: () -> Unit,
    onSave: (lunch: Boolean, dinner: Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        tonalElevation = 2.dp,
    ) {
        var lunch by remember { mutableStateOf(dayStatus?.lunchCheckedIn ?: false) }
        var dinner by remember { mutableStateOf(dayStatus?.dinnerCheckedIn ?: false) }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            // 日期标题
            Text(
                text = "${date.year}年${date.monthNumber}月${date.dayOfMonth}日",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = date.dayOfWeek.displayName(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 餐次设置
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    MealToggleItem(
                        mealType = MealType.LUNCH,
                        checked = lunch,
                        onCheckedChange = { lunch = it },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    MealToggleItem(
                        mealType = MealType.DINNER,
                        checked = dinner,
                        onCheckedChange = { dinner = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilledButton(
                onClick = { onSave(lunch, dinner); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Text("保存修改")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
```

#### 2.2.4 设置页面 (SettingsScreen)

**当前问题**:

- 自定义 `SettingsRow` 实现，与 MD3 `ListItem` 功能重复
- Card 嵌套简单内容，过于笨重
- 缺少图标容器（Icon Button / ListItem 的 leading icon 样式）

**优化方案**:

```kotlin
@Composable
fun SettingsScreen(...) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 使用 MD3 ListItem 替代自定义 SettingsRow
        ListItem(
            headlineContent = { Text("每日签到提醒") },
            supportingContent = { Text("午餐 11:30 · 晚餐 17:30") },
            leadingContent = {
                Icon(Icons.Rounded.Notifications, contentDescription = null)
            },
            trailingContent = {
                Switch(checked = enabled, onCheckedChange = onToggle)
            },
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text("导出签到数据") },
            supportingContent = { Text("导出为 CSV 文件") },
            leadingContent = {
                Icon(Icons.Rounded.FileDownload, contentDescription = null)
            },
            modifier = Modifier.clickable { onExport() },
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text("清除所有数据", color = MaterialTheme.colorScheme.error) },
            supportingContent = { Text("删除全部签到记录，不可恢复") },
            leadingContent = {
                Icon(
                    Icons.Rounded.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            modifier = Modifier.clickable { showConfirmDialog = true },
        )
    }
}
```

**分组策略**:

- 使用 `Surface` + `tonalElevation` 分组替代 Card 嵌套
- 每组之间用 16dp 间距 + 标题分隔

```
┌──────────────────────────────────┐
│  设置                            │  ← headlineSmall, 顶部留 16dp
├──────────────────────────────────┤
│  ┌────────────────────────────┐  │
│  │  🔔 每日签到提醒      [开关] │  │  ← 第一组：提醒
│  │     午餐 11:30 · 晚餐 17:30 │  │
│  └────────────────────────────┘  │  ← Surface tonalElevation=1
│                                  │
│  ┌────────────────────────────┐  │
│  │  📥 导出签到数据      >     │  │  ← 第二组：数据
│  │     导出为 CSV 文件         │  │
│  ├────────────────────────────┤  │
│  │  🗑 清除所有数据       >     │  │
│  │     删除全部，不可恢复       │  │     error 色
│  └────────────────────────────┘  │
└──────────────────────────────────┘
```

---

### 2.3 全局优化

#### 2.3.1 导航切换动效

```kotlin
NavHost(
    navController = navController,
    startDestination = BottomNavItem.CHECK_IN.route,
    modifier = Modifier.padding(paddingValues),
    enterTransition = {
        fadeIn(animationSpec = tween(300))
    },
    exitTransition = {
        fadeOut(animationSpec = tween(300))
    },
) { ... }
```

底部导航切换使用 **Fade** 过渡（Material Motion 推荐）。

#### 2.3.2 Snackbar 配置

```kotlin
Scaffold(
    snackbarHost = {
        SnackbarHost(snackbarHostState) { data ->
            Snackbar(
                snackbarData = data,
                shape = MaterialTheme.shapes.small,
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
    },
    // ...
)
```

使用 `inverseSurface` 确保 Snackbar 从任何背景中都清晰可见。

#### 2.3.3 Scaffold Padding & Edge-to-Edge

```kotlin
Scaffold(
    modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.navigationBars),
    // ...
) { innerPadding ->
    NavHost(
        modifier = Modifier.padding(innerPadding),
        // ...
    )
}
```

确保 edge-to-edge 时内容不被导航栏遮挡。

#### 2.3.4 Pull-to-Refresh

在签到首页增加下拉刷新：

```kotlin
val pullRefreshState = rememberPullToRefreshState()

Box(
    modifier = Modifier
        .nestedScroll(pullRefreshState.nestedScrollConnection)
        .fillMaxSize(),
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // ... 页面内容
    }

    PullToRefreshContainer(
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
    )
}
```

---

### 2.4 无障碍优化清单

| 项                       | 现状                  | 优化后                                         |
|-------------------------|---------------------|---------------------------------------------|
| Icon contentDescription | 多处为 null            | 全部补全描述                                      |
| 签到按钮                    | 无状态朗读               | 添加 `semantics { stateDescription = "已签到" }` |
| DonutChart              | 无法被屏幕阅读器识别          | 添加 `contentDescription` 描述统计数据              |
| 日期格子                    | 只读数字                | 添加 `contentDescription = "2月15日，已签到2次"`     |
| 触摸目标                    | 日历日期 aspectRatio=1f | 确保最小 48dp × 48dp                            |

---

## 3. 文件变更清单

### 3.1 新增文件

| 路径                                    | 说明                      |
|---------------------------------------|-------------------------|
| `ui/theme/Type.kt`                    | Typography 定义           |
| `ui/theme/Shape.kt`                   | Shape 定义（可合并到 Theme.kt） |
| `ui/theme/Spacing.kt`                 | Spacing Tokens          |
| `ui/components/MealCheckInButton.kt`  | 新版签到按钮                  |
| `ui/components/DayStatusIndicator.kt` | 日期状态指示器                 |
| `ui/components/MealToggleItem.kt`     | 底部弹窗餐次切换项               |

### 3.2 修改文件

| 路径                               | 变更内容                                     |
|----------------------------------|------------------------------------------|
| `ui/theme/Color.kt`              | 补全语义色                                    |
| `ui/theme/Theme.kt`              | 集成 Typography、Shape、补全 ColorScheme       |
| `ui/checkin/CheckInScreen.kt`    | 使用新版按钮、AssistChip、haptic、pull-to-refresh |
| `ui/calendar/CalendarScreen.kt`  | 去除多余 Card、手势切换、日期状态新样式                   |
| `ui/calendar/DayDetailDialog.kt` | 改为 ModalBottomSheet                      |
| `ui/settings/SettingsScreen.kt`  | 使用 ListItem、Surface 分组                   |
| `ui/components/DonutChart.kt`    | 无障碍、图例优化                                 |
| `navigation/MainNavHost.kt`      | 导航动效、Scaffold padding 调整                 |

---

## 4. 视觉对比预览

### 4.1 签到首页

| 区域   | Before        | After                                |
|------|---------------|--------------------------------------|
| 欢迎标语 | Card 容器，与下方同级 | Surface + tonalElevation，视觉后退        |
| 签到按钮 | Card，按压无反馈    | FilledTonalButton，自带 ripple + haptic |
| 统计图表 | Card 容器       | ElevatedCard，微微浮起                    |
| 上限提示 | 普通 Text       | AssistChip，更醒目                       |

### 4.2 日历页面

| 区域   | Before              | After                  |
|------|---------------------|------------------------|
| 整体   | 多块 Card 嵌套          | 统一 Surface，减少视觉噪音      |
| 日期状态 | 统一绿色圆点              | 空心圆(1次) + 实心圆(2次)，语义清晰 |
| 今日标记 | primaryContainer 填充 | Outline 边框圆圈，不冲突       |
| 月份切换 | 仅按钮                 | 按钮 + 左右滑动手势            |

### 4.3 设置页面

| 区域   | Before          | After                            |
|------|-----------------|----------------------------------|
| 列表项  | 自定义 SettingsRow | MD3 ListItem，规范一致                |
| 分组   | Card 包裹         | Surface tonalElevation 分组        |
| 危险操作 | 普通文字红色          | ListItem + error 色图标 + 确认 Dialog |

---

## 5. 实现优先级

| 优先级 | 内容                                 | 预估工时  |
|-----|------------------------------------|-------|
| P0  | Typography + Color + Shape 系统搭建    | 0.5 天 |
| P0  | Theme.kt 集成新 Design System         | 0.5 天 |
| P1  | CheckInScreen 重构（按钮、图表、动效）         | 1 天   |
| P1  | CalendarScreen 重构（层级、手势、日期样式）      | 1 天   |
| P1  | DayDetailDialog → ModalBottomSheet | 0.5 天 |
| P2  | SettingsScreen 重构（ListItem、分组）     | 0.5 天 |
| P2  | 导航动效 + Snackbar 样式                 | 0.5 天 |
| P2  | 无障碍全面补全                            | 0.5 天 |
| P3  | Pull-to-Refresh                    | 0.5 天 |

**总计**: ~5 天

---

## 6. 参考规范

- [Material Design 3 — Components](https://m3.material.io/components)
- [Material Design 3 — Motion](https://m3.material.io/styles/motion)
- [Compose Material 3 API](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary)
- [Material 3 Color System](https://m3.material.io/styles/color/system/how-the-system-works)
