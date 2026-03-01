# Pokémon App — 宝可梦图鉴助手 PRD

> **版本**: v1.0.0  
> **最后更新**: 2026-03-01  
> **模块**: `apps/pokemon` · `shareds/pokemon`  
> **数据来源**: [PokeAPI v2](https://pokeapi.co/docs/v2)

---

## 1. 产品概述

### 1.1 产品定位

Pokémon App 是一款**全方位宝可梦百科与工具类应用**，基于 PokeAPI
开放数据，为宝可梦爱好者提供图鉴浏览、属性查询、队伍构建、对战辅助等功能。

### 1.2 目标用户

- 宝可梦系列游戏玩家（新手与核心向）
- 宝可梦 TCG / 竞技对战爱好者
- 希望了解宝可梦世界观的泛兴趣用户

### 1.3 核心价值

| 价值点  | 说明                      |
|------|-------------------------|
| 全面图鉴 | 覆盖全世代 1000+ 宝可梦，随时查阅    |
| 对战辅助 | 属性克制速查、种族值对比、队伍分析       |
| 离线可用 | 本地缓存核心数据，无网络也能使用        |
| 多语言  | 支持中 / 英 / 日等多语言名称显示     |
| 精美展示 | 官方 Sprite 图 + 叫声播放，沉浸体验 |

---

## 2. 功能模块总览

| 编号  | 模块      | 优先级 | 对应 API                                   |
|-----|---------|-----|------------------------------------------|
| F1  | 宝可梦图鉴   | P0  | `/pokemon`, `/pokemon-species`           |
| F2  | 宝可梦详情   | P0  | `/pokemon/{id}`, `/pokemon-species/{id}` |
| F3  | 进化链查看器  | P0  | `/evolution-chain/{id}`                  |
| F4  | 属性克制计算器 | P0  | `/type/{id}`                             |
| F5  | 招式数据库   | P1  | `/move`, `/move/{id}`                    |
| F6  | 特性百科    | P1  | `/ability/{id}`                          |
| F7  | 道具图鉴    | P1  | `/item/{id}`                             |
| F8  | 地区与地点探索 | P1  | `/region`, `/location`, `/location-area` |
| F9  | 树果指南    | P2  | `/berry`, `/berry-flavor`                |
| F10 | 性格指南    | P2  | `/nature/{id}`                           |
| F11 | 队伍构建器   | P1  | 多接口综合                                    |
| F12 | 宝可梦对比   | P1  | `/pokemon/{id}`                          |
| F13 | 收藏与本地图鉴 | P1  | 本地数据库                                    |
| F14 | 搜索与筛选   | P0  | 多接口综合                                    |
| F15 | 宝可梦小测验  | P2  | 多接口综合                                    |
| F16 | 世代浏览    | P2  | `/generation/{id}`, `/pokedex/{id}`      |

---

## 3. 功能需求详述

### F1 — 宝可梦图鉴（P0 · 核心）

#### 3.1.1 图鉴列表页

- **无限滚动列表**：使用分页接口 (`?limit=20&offset=0`)，下拉加载更多。
- **每项展示**：
    - 编号（全国图鉴 #001）
    - 名称（支持中/英/日切换）
    - 缩略图（`sprites.front_default`）
    - 属性标签（如 🔥火 / 🌿草），用对应颜色展示
- **快速定位**：侧边字母/编号索引条，一键跳转到指定区间。
- **已收藏标记**：标注用户已收藏的宝可梦。

#### 3.1.2 数据来源

| API 端点                                  | 用途         |
|-----------------------------------------|------------|
| `GET /api/v2/pokemon?limit=20&offset=0` | 分页获取列表     |
| `GET /api/v2/pokemon/{id}`              | 获取精灵图、属性   |
| `GET /api/v2/pokemon-species/{id}`      | 获取多语言名称、分类 |

---

### F2 — 宝可梦详情页（P0 · 核心）

#### 3.2.1 基础信息卡片

| 展示内容 | 数据字段               | 说明              |
|------|--------------------|-----------------|
| 编号   | `id`               | 全国图鉴编号          |
| 名称   | `species.names[]`  | 多语言名称           |
| 分类   | `species.genera[]` | 如「种子宝可梦」        |
| 身高   | `height`           | 转换为 m 展示 (÷10)  |
| 体重   | `weight`           | 转换为 kg 展示 (÷10) |
| 属性   | `types[]`          | 彩色属性标签          |
| 基础经验 | `base_experience`  | 击败可获得经验         |

#### 3.2.2 精灵图展示

- **多角度查看**：正面 / 背面 / 闪光（Shiny）/ 性别差异 (`sprites`)
- **动画切换**：支持默认与 Shiny 版本一键切换
- **叫声播放**：调用 `cries.latest` 或 `cries.legacy` 播放宝可梦叫声 🔊

#### 3.2.3 种族值雷达图

| 种族值 | 字段                                                  |
|-----|-----------------------------------------------------|
| HP  | `stats[].base_stat` (stat.name = "hp")              |
| 攻击  | `stats[].base_stat` (stat.name = "attack")          |
| 防御  | `stats[].base_stat` (stat.name = "defense")         |
| 特攻  | `stats[].base_stat` (stat.name = "special-attack")  |
| 特防  | `stats[].base_stat` (stat.name = "special-defense") |
| 速度  | `stats[].base_stat` (stat.name = "speed")           |

- 使用 **雷达图 (Radar Chart)** 可视化六维种族值。
- 显示种族值总和 (BST)。
- 每项旁标注 EV (努力值) 产出 (`stats[].effort`)。

#### 3.2.4 特性列表

- 展示所有可能特性 (`abilities[]`)，区分普通特性与隐藏特性 (`is_hidden`)。
- 点击特性名可跳转至 **F6 特性百科** 查看详细效果。

#### 3.2.5 招式列表

- 分 Tab 展示：升级学习 / TM/HM 教学 / 遗传招式 / 导师教授（按 `move_learn_method` 分类）。
- 每项显示：招式名称、属性、分类（物理/特殊/变化）、威力、命中、PP。
- 点击可跳转至 **F5 招式详情**。

#### 3.2.6 图鉴描述

- 展示各版本的 Flavor Text (`species.flavor_text_entries[]`)。
- 可按游戏版本筛选。

#### 3.2.7 栖息地与遭遇

- 展示该宝可梦出没的地点 (`location_area_encounters`)。
- 按游戏版本分组，显示遭遇概率与等级范围。

#### 3.2.8 形态变化

- 如果有多种形态 (`forms[]`)，展示所有形态的精灵图与属性差异。
- 标注是否为 Mega 进化形态 (`is_mega`)、战斗专属形态 (`is_battle_only`)。

---

### F3 — 进化链查看器（P0 · 核心）

#### 3.3.1 进化树可视化

- 以**流程图/树状图**展示完整进化链 (`evolution-chain`)。
- 每个节点展示：宝可梦精灵图 + 名称。
- 节点之间展示进化条件。

#### 3.3.2 进化条件展示

支持展示 PokeAPI 提供的所有进化条件：

| 条件       | 字段                               | 示例        |
|----------|----------------------------------|-----------|
| 等级       | `min_level`                      | Lv.16     |
| 使用道具     | `item`                           | 雷之石       |
| 交换       | `trigger = trade`                | 通信交换      |
| 携带物品交换   | `held_item`                      | 携带王者之证交换  |
| 好感度      | `min_happiness`                  | 好感度 ≥ 220 |
| 美丽度      | `min_beauty`                     | 美丽度 ≥ 170 |
| 时间       | `time_of_day`                    | 白天/夜晚     |
| 性别       | `gender`                         | 仅限♀       |
| 地点       | `location`                       | 苔岩附近      |
| 已知招式     | `known_move` / `known_move_type` | 已学会某招式    |
| 队伍中同行    | `party_species` / `party_type`   | 队伍中有某属性   |
| 天气       | `needs_overworld_rain`           | 需要下雨      |
| 物攻 vs 物防 | `relative_physical_stats`        | 攻击 > 防御   |
| 倒置主机     | `turn_upside_down`               | 倒置 3DS    |
| 地区       | `region`                         | 特定地区      |

---

### F4 — 属性克制计算器（P0 · 核心）

#### 3.4.1 属性相克表

- 以**矩阵表格**展示 18 种属性之间的相克关系。
- 数据来源：`type.damage_relations`
    - `double_damage_to` → 效果拔群（2×）
    - `half_damage_to` → 效果不佳（0.5×）
    - `no_damage_to` → 完全无效（0×）
    - `double_damage_from` / `half_damage_from` / `no_damage_from` → 防御端

#### 3.4.2 单属性查询

- 选择一种属性，展示：
    - 攻击时：克制（2×）/ 被抗（0.5×）/ 无效（0×）的属性列表
    - 防御时：弱点（2×）/ 抵抗（0.5×）/ 免疫（0×）的属性列表

#### 3.4.3 双属性组合计算

- 输入双属性组合（如 水/地面），自动计算综合防御端倍率。
- 展示：4× 弱点 / 2× 弱点 / 1× 普通 / 0.5× 抵抗 / 0.25× 强抵抗 / 0× 免疫。

#### 3.4.4 历史属性变更

- 利用 `past_damage_relations` 展示不同世代的属性克制差异。
- 例如：钢属性在第六世代不再抵抗恶属性与鬼属性。

---

### F5 — 招式数据库（P1）

#### 3.5.1 招式列表

- 支持按**属性 / 分类（物理·特殊·变化）/ 世代**筛选。
- 每项显示：名称、属性图标、分类图标、威力、命中率、PP。

#### 3.5.2 招式详情

| 展示内容   | 数据字段                                     |
|--------|------------------------------------------|
| 名称     | `names[]` (多语言)                          |
| 属性     | `type`                                   |
| 分类     | `damage_class` (physical/special/status) |
| 威力     | `power`                                  |
| 命中率    | `accuracy`                               |
| PP     | `pp`                                     |
| 优先级    | `priority`                               |
| 效果描述   | `effect_entries[]`                       |
| 风味文本   | `flavor_text_entries[]`                  |
| 对战元数据  | `meta` (击中次数、回合数、吸血率、暴击率、异常概率等)          |
| 可学会宝可梦 | `learned_by_pokemon[]`                   |
| 世代     | `generation`                             |
| 属性变更   | `stat_changes[]`                         |
| 历史数值   | `past_values[]`                          |

#### 3.5.3 华丽大赛信息

- 展示招式在华丽大赛中的表现 (`contest_type`, `contest_effect`)。
- 展示连锁招式组合 (`contest_combos`)。

---

### F6 — 特性百科（P1）

#### 3.6.1 特性列表

- 展示所有特性，可按世代、名称搜索。
- 标注是否为正作特性 (`is_main_series`)。

#### 3.6.2 特性详情

| 展示内容      | 数据字段                       |
|-----------|----------------------------|
| 名称        | `names[]` (多语言)            |
| 效果        | `effect_entries[]` (详细/简短) |
| 风味文本      | `flavor_text_entries[]`    |
| 拥有此特性的宝可梦 | `pokemon[]`（标注是否隐藏特性、槽位）   |
| 初始世代      | `generation`               |
| 效果变更历史    | `effect_changes[]`         |

---

### F7 — 道具图鉴（P1）

#### 3.7.1 道具列表

- 按分类浏览：精灵球、回复药、进化石、招式机、特殊道具等 (`item-category`)。
- 按口袋分组 (`item-pocket`)。

#### 3.7.2 道具详情

| 展示内容    | 数据字段                           |
|---------|--------------------------------|
| 名称      | `names[]`                      |
| 图标      | `sprites.default`              |
| 价格      | `cost`                         |
| 效果      | `effect_entries[]`             |
| 风味文本    | `flavor_text_entries[]`        |
| 属性标签    | `attributes[]` (可消耗、可持有等)      |
| 投掷威力    | `fling_power` / `fling_effect` |
| 野生宝可梦持有 | `held_by_pokemon[]`            |
| 对应进化链   | `baby_trigger_for`             |
| 对应招式学习器 | `machines[]`                   |

---

### F8 — 地区与地点探索（P1）

#### 3.8.1 地区总览

- 展示所有地区 (`region`)：关都、城都、丰缘、神奥……
- 每个地区展示关联的世代、图鉴、地点数量。

#### 3.8.2 地点列表

- 按地区筛选所有地点 (`location`)。
- 展示地点名称（多语言）、所属子区域。

#### 3.8.3 区域详情 & 宝可梦遭遇

- 展示地点子区域 (`location-area`) 中可遭遇的宝可梦。
- 按版本分组，显示：
    - 遭遇方式（草丛、钓鱼、冲浪等 `encounter_method`）
    - 遭遇概率 (`chance`)
    - 等级范围 (`min_level` ~ `max_level`)
    - 遭遇条件 (`condition_values`：时间、天气等)

#### 3.8.4 伙伴公园 (Pal Park)

- 展示 Pal Park 各区域 (`pal-park-area`) 的宝可梦分布。
- 显示基础分数与遭遇概率。

---

### F9 — 树果指南（P2）

#### 3.9.1 树果列表

- 展示所有树果及其基本信息。

#### 3.9.2 树果详情

| 展示内容   | 数据字段                         |
|--------|------------------------------|
| 名称     | `name`                       |
| 成长时间   | `growth_time`（小时 × 4 阶段）     |
| 最大收获   | `max_harvest`                |
| 大小     | `size` (mm)                  |
| 顺滑度    | `smoothness`                 |
| 土壤干燥速率 | `soil_dryness`               |
| 硬度     | `firmness`                   |
| 口味     | `flavors[]` (辣/涩/甜/苦/酸 + 强度) |
| 自然之恩属性 | `natural_gift_type`          |
| 自然之恩威力 | `natural_gift_power`         |
| 对应道具   | `item`                       |

#### 3.9.3 口味与性格关联

- 口味 (`berry-flavor`) 关联华丽大赛类型 (`contest_type`)。
- 结合性格数据，展示哪些性格喜欢/讨厌哪些口味。

---

### F10 — 性格指南（P2）

#### 3.10.1 性格列表

- 展示全部 25 种性格。

#### 3.10.2 性格详情

| 展示内容   | 数据字段                              |
|--------|-----------------------------------|
| 名称     | `names[]` (多语言)                   |
| 能力值加成  | `increased_stat` (+10%)           |
| 能力值减成  | `decreased_stat` (-10%)           |
| 喜欢口味   | `likes_flavor`                    |
| 讨厌口味   | `hates_flavor`                    |
| 宝可竞技影响 | `pokeathlon_stat_changes[]`       |
| 对战殿堂偏好 | `move_battle_style_preferences[]` |

#### 3.10.3 性格速查矩阵

- 以 5×5 矩阵展示所有性格对能力值的影响（行为加成，列为减成）。
- 对角线（无影响性格）特殊标注。

---

### F11 — 队伍构建器（P1）

#### 3.11.1 添加宝可梦

- 从图鉴中选择最多 **6 只**宝可梦组成队伍。
- 可搜索名称或编号快速添加。

#### 3.11.2 属性覆盖分析

- 以矩阵展示队伍每只宝可梦的属性弱点与抗性。
- 高亮显示属性盲区（例如全队都怕地面系）。
- 统计队伍属性覆盖率（攻击端可打击多少种属性的效果拔群）。

#### 3.11.3 种族值对比

- 以叠加柱状图展示队伍所有成员的种族值分布。
- 标注队伍平均种族值、总和。

#### 3.11.4 队伍保存

- 支持保存多套队伍方案至本地数据库。
- 可命名、备注。

---

### F12 — 宝可梦对比（P1）

#### 3.12.1 选择对比

- 选择 2~4 只宝可梦进行对比。

#### 3.12.2 对比维度

| 维度    | 说明          |
|-------|-------------|
| 种族值   | 六维雷达图叠加     |
| 属性    | 克制关系互相比较    |
| 特性    | 列出各自可用特性    |
| 身高/体重 | 数值对比        |
| 招式池   | 共有招式 / 独有招式 |
| 进化阶段  | 进化链位置       |

---

### F13 — 收藏与本地图鉴（P1）

#### 3.13.1 收藏功能

- 任何宝可梦详情页可点击 ❤️ 收藏。
- 收藏数据存储于本地 Room 数据库。

#### 3.13.2 完成进度

- 展示用户已浏览/已收藏的宝可梦占全部宝可梦的比例。
- 按世代 / 地区展示收集进度条。

#### 3.13.3 笔记功能

- 用户可为每只宝可梦添加个人笔记（对战心得、培养计划等）。

---

### F14 — 搜索与筛选（P0 · 核心）

#### 3.14.1 全局搜索

- 支持按名称（中/英/日）、编号搜索宝可梦。
- 搜索结果实时匹配，支持模糊搜索。

#### 3.14.2 高级筛选

| 筛选条件  | 来源                                     |
|-------|----------------------------------------|
| 属性    | `types[]` — 18 种属性多选                   |
| 世代    | `generation` — 第一~第九世代                 |
| 颜色    | `pokemon-color` — 黑/蓝/棕/灰/绿/粉/紫/红/白/黄  |
| 形状    | `pokemon-shape` — 球形/四足/双足等            |
| 栖息地   | `pokemon-habitat` — 洞穴/森林/草原/山地/海洋/城市等 |
| 蛋组    | `egg-group` — 怪兽组/水中1组/飞行组等            |
| 成长速率  | `growth-rate` — 最快/快/中快/中慢/慢/最慢        |
| 种族值范围 | 自定义区间筛选                                |
| 传说/幻  | `is_legendary` / `is_mythical`         |
| 宝宝宝可梦 | `is_baby`                              |

#### 3.14.3 排序

- 按编号、名称、种族值总和、身高、体重、捕获率等排序。

---

### F15 — 宝可梦小测验（P2）

#### 3.15.1 剪影猜猜看

- 展示宝可梦剪影（处理 `sprites.front_default` 为纯黑），用户猜名字。
- 支持按世代选择题库范围。

#### 3.15.2 叫声辨识

- 播放 `cries.latest`，用户从 4 个选项中选择正确的宝可梦。

#### 3.15.3 属性问答

- 随机出题：「皮卡丘是什么属性？」「水系克制什么？」

#### 3.15.4 谁的种族值更高

- 展示两只宝可梦，用户猜测谁的某项种族值更高。

#### 3.15.5 成绩记录

- 本地记录答对率、连胜记录。

---

### F16 — 世代浏览（P2）

#### 3.16.1 世代列表

- 展示全部世代 (`generation`)，包含：
    - 世代名称
    - 主要地区 (`main_region`)
    - 新增宝可梦数量 (`pokemon_species[]`)
    - 新增招式数量 (`moves[]`)
    - 新增特性数量 (`abilities[]`)
    - 新增属性 (`types[]`)

#### 3.16.2 地区图鉴

- 按地区图鉴 (`pokedex`) 浏览宝可梦。
- 展示区域图鉴编号 (`pokemon_entries[].entry_number`)。

#### 3.16.3 版本信息

- 展示各游戏版本 (`version`) 及其版本组 (`version-group`)。
- 对应地区、图鉴、可用招式学习方法等。

---

## 4. 非功能需求

### 4.1 数据链路设计（Offline-First）

由于 PokeAPI 接口的稳定性存在不确定性（网络波动、海外 CDN 访问延迟、限流等），采用 **API → Room → UI**
的离线优先缓存策略，确保即使网络请求失败，UI 仍能从本地数据库展示已缓存的内容。

#### 4.1.1 数据流架构图

```
┌─────────┐     ┌──────────────┐     ┌──────────┐     ┌─────────┐
│ PokeAPI │────▶│  Repository  │────▶│   Room   │────▶│   UI    │
│ (远程)   │     │ (协调层)      │     │ (本地DB) │     │ (Compose)│
└─────────┘     └──────────────┘     └──────────┘     └─────────┘
                       │                    ▲
                       │   ┌────────────────┘
                       │   │ Flow 订阅
                       ▼   │
                 ┌──────────────┐
                 │  ViewModel   │
                 │  (StateFlow) │
                 └──────────────┘
```

#### 4.1.2 数据请求策略

| 策略                                        | 说明                                                    |
|-------------------------------------------|-------------------------------------------------------|
| **Room 作为唯一数据源 (Single Source of Truth)** | UI 层始终从 Room Flow 订阅数据，不直接消费 API 响应                   |
| **请求时写入**                                 | Repository 调用 PokeAPI 获取数据后，立即写入 Room，Flow 自动通知 UI 更新 |
| **失败静默降级**                                | 网络请求失败时不报错（除首次加载），UI 展示 Room 中已缓存的数据                  |
| **首次加载提示**                                | Room 无数据且网络请求失败时，展示空状态页面 + 重试按钮                       |
| **智能刷新**                                  | 根据 `last_updated_at` 字段判断缓存是否过期，过期才发起网络请求             |

#### 4.1.3 缓存更新策略

```kotlin
// Repository 伪代码
fun getPokemonDetail(id: Int): Flow<PokemonDetail?> {
    return pokemonDao.getPokemonById(id)     // 1. 立即返回 Room 缓存（Flow）
        .onStart {
            refreshFromApi(id)                // 2. 同时发起网络请求
        }
}

private suspend fun refreshFromApi(id: Int) {
    val cached = pokemonDao.getPokemonByIdOnce(id)
    if (cached != null && !cached.isExpired()) return  // 缓存未过期，跳过

    when (val result = api.getPokemon(id)) {           // 3. 请求 API
        is BffResult.Success -> {
            pokemonDao.upsert(result.response.toEntity()) // 4. 写入 Room
            // Room Flow 自动推送新数据给 UI
        }
        is BffResult.Failure -> {
            // 静默失败，UI 继续展示旧缓存
            // 仅在 Room 无数据时通知 UI 展示错误状态
        }
    }
}
```

#### 4.1.4 缓存时效

| 数据类型     | 缓存有效期 | 说明                   |
|----------|-------|----------------------|
| 宝可梦基础数据  | 30 天  | 种族值、属性等极少变动          |
| 宝可梦列表    | 7 天   | 新宝可梦发布时需更新           |
| 招式/特性/道具 | 30 天  | 基本不变                 |
| 进化链      | 30 天  | 极少变动                 |
| 精灵图 URL  | 永不过期  | URL 稳定，图片由 Coil 磁盘缓存 |
| 属性克制关系   | 永不过期  | 游戏规则基本不变             |

#### 4.1.5 Room 缓存表设计

每个 API 实体对应一张 Room 表，核心表如下：

| 表名                | 主键                        | 核心字段                                                                                                                            | 用途     |
|-------------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------------|--------|
| `pokemon_list`    | `id: Int`                 | name, sprite_url, types_json, order                                                                                             | 图鉴列表   |
| `pokemon_detail`  | `id: Int`                 | name, height, weight, base_experience, stats_json, abilities_json, sprites_json, cries_json, species_id                         | 详情页    |
| `pokemon_species` | `id: Int`                 | names_json, genera_json, flavor_texts_json, evolution_chain_id, generation_id, is_legendary, is_mythical, habitat, color, shape | 物种信息   |
| `evolution_chain` | `id: Int`                 | chain_json                                                                                                                      | 进化链    |
| `type`            | `id: Int`                 | name, damage_relations_json, names_json                                                                                         | 属性克制   |
| `move`            | `id: Int`                 | name, type_id, power, accuracy, pp, damage_class, effect_json, meta_json                                                        | 招式     |
| `ability`         | `id: Int`                 | name, effect_json, flavor_text_json, pokemon_json                                                                               | 特性     |
| `item`            | `id: Int`                 | name, cost, sprite_url, effect_json, category                                                                                   | 道具     |
| `nature`          | `id: Int`                 | name, increased_stat, decreased_stat, likes_flavor, hates_flavor                                                                | 性格     |
| `cache_metadata`  | `entity_type + entity_id` | last_updated_at                                                                                                                 | 缓存时效控制 |

> JSON 字段使用 `kotlinx.serialization` 序列化后以 `TEXT` 类型存储，Room TypeConverter 负责转换。

### 4.2 其它缓存策略

| 策略   | 说明                    |
|------|-----------------------|
| 图片缓存 | Sprite 图使用 Coil 的磁盘缓存 |
| 增量加载 | 图鉴列表按需分页加载，不一次性拉全量    |

### 4.2 性能要求

| 指标   | 目标            |
|------|---------------|
| 列表首屏 | ≤ 1.5s（含网络请求） |
| 详情页  | ≤ 1s          |
| 搜索响应 | ≤ 300ms（本地匹配） |
| 内存占用 | ≤ 150MB       |

### 4.3 技术选型建议

| 技术   | 选择                                |
|------|-----------------------------------|
| 架构   | MVVM + Clean Architecture         |
| 网络   | Ktor Client (KMP)                 |
| 序列化  | Kotlinx Serialization             |
| 数据库  | Room (Android) / SQLDelight (KMP) |
| 图片加载 | Coil 3 (KMP)                      |
| UI   | Jetpack Compose                   |
| 图表   | Vico / 自定义 Canvas                 |
| 音频   | ExoPlayer / MediaPlayer (叫声播放)    |
| DI   | Koin / Hilt                       |

### 4.4 API 使用注意事项

- PokeAPI 是**只读 API**，仅支持 GET 请求。
- 无需认证，但需遵守公平使用政策（Fair Use Policy）。
- **必须做本地缓存**，减少对服务器的请求频率。
- 所有文本数据包含多语言版本，通过 `language.name` 字段筛选。
- Sprite 图托管在 GitHub (`raw.githubusercontent.com`)，注意国内网络访问性。

---

## 5. PokeAPI 端点速查表

| 分类       | 端点                                        | 说明                   |
|----------|-------------------------------------------|----------------------|
| **宝可梦**  | `/pokemon/{id or name}`                   | 宝可梦基础数据（种族值、精灵图、招式等） |
|          | `/pokemon-species/{id or name}`           | 物种数据（图鉴描述、进化链、蛋组等）   |
|          | `/pokemon/{id}/encounters`                | 宝可梦出没地点              |
|          | `/pokemon-form/{id or name}`              | 宝可梦形态信息              |
|          | `/pokemon-color/{id or name}`             | 按颜色分类                |
|          | `/pokemon-shape/{id or name}`             | 按形状分类                |
|          | `/pokemon-habitat/{id or name}`           | 按栖息地分类               |
|          | `/ability/{id or name}`                   | 特性详情                 |
|          | `/characteristic/{id}`                    | 个体值特征                |
|          | `/egg-group/{id or name}`                 | 蛋组                   |
|          | `/gender/{id or name}`                    | 性别相关                 |
|          | `/growth-rate/{id or name}`               | 成长速率                 |
|          | `/nature/{id or name}`                    | 性格                   |
|          | `/pokeathlon-stat/{id or name}`           | 宝可竞技数据               |
|          | `/stat/{id or name}`                      | 能力值定义                |
|          | `/type/{id or name}`                      | 属性克制关系               |
| **招式**   | `/move/{id or name}`                      | 招式详情                 |
|          | `/move-ailment/{id or name}`              | 招式异常状态               |
|          | `/move-battle-style/{id or name}`         | 对战风格                 |
|          | `/move-category/{id or name}`             | 招式分类                 |
|          | `/move-damage-class/{id or name}`         | 伤害类型                 |
|          | `/move-learn-method/{id or name}`         | 学习方式                 |
|          | `/move-target/{id or name}`               | 招式目标                 |
| **进化**   | `/evolution-chain/{id}`                   | 进化链                  |
|          | `/evolution-trigger/{id or name}`         | 进化触发条件               |
| **道具**   | `/item/{id or name}`                      | 道具详情                 |
|          | `/item-attribute/{id or name}`            | 道具属性                 |
|          | `/item-category/{id or name}`             | 道具分类                 |
|          | `/item-fling-effect/{id or name}`         | 投掷效果                 |
|          | `/item-pocket/{id or name}`               | 背包口袋                 |
| **树果**   | `/berry/{id or name}`                     | 树果详情                 |
|          | `/berry-firmness/{id or name}`            | 树果硬度                 |
|          | `/berry-flavor/{id or name}`              | 树果口味                 |
| **地点**   | `/location/{id or name}`                  | 地点                   |
|          | `/location-area/{id or name}`             | 地点子区域                |
|          | `/region/{id or name}`                    | 地区                   |
|          | `/pal-park-area/{id or name}`             | 伙伴公园区域               |
| **游戏**   | `/generation/{id or name}`                | 世代                   |
|          | `/pokedex/{id or name}`                   | 图鉴                   |
|          | `/version/{id or name}`                   | 游戏版本                 |
|          | `/version-group/{id or name}`             | 版本组                  |
| **华丽大赛** | `/contest-type/{id or name}`              | 大赛类型                 |
|          | `/contest-effect/{id}`                    | 大赛效果                 |
|          | `/super-contest-effect/{id}`              | 超级大赛效果               |
| **遭遇**   | `/encounter-method/{id or name}`          | 遭遇方式                 |
|          | `/encounter-condition/{id or name}`       | 遭遇条件                 |
|          | `/encounter-condition-value/{id or name}` | 遭遇条件值                |
| **机器**   | `/machine/{id}`                           | TM/HM 对应关系           |
| **工具**   | `/language/{id or name}`                  | 支持语言                 |

---

## 6. 开发路线建议

### Phase 1 — MVP（4~6 周）

- [x] 项目与模块搭建 (`apps/pokemon`, `shareds/pokemon`)
- [ ] F14 搜索与筛选（基础版）
- [ ] F1 宝可梦图鉴列表
- [ ] F2 宝可梦详情页（基础信息 + 种族值 + 精灵图）
- [ ] F4 属性克制计算器
- [ ] F13 收藏功能
- [ ] 本地缓存层

### Phase 2 — 丰富内容（3~4 周）

- [ ] F3 进化链查看器
- [ ] F5 招式数据库
- [ ] F6 特性百科
- [ ] F2 补充：招式列表、遭遇地点、图鉴描述
- [ ] F11 队伍构建器
- [ ] F12 宝可梦对比

### Phase 3 — 完善体验（2~3 周）

- [ ] F7 道具图鉴
- [ ] F8 地区与地点探索
- [ ] F9 树果指南
- [ ] F10 性格指南
- [ ] F16 世代浏览
- [ ] F15 宝可梦小测验
- [ ] F2 补充：叫声播放、形态变化

---

## 7. 参考链接

| 资源                 | 地址                                       |
|--------------------|------------------------------------------|
| PokeAPI 文档         | https://pokeapi.co/docs/v2               |
| PokeAPI GitHub     | https://github.com/PokeAPI/pokeapi       |
| PokeAPI GraphQL    | https://beta.pokeapi.co/graphql/console/ |
| Kotlin 多平台 Wrapper | https://github.com/PokeAPI/pokekotlin    |
| 精灵图仓库              | https://github.com/PokeAPI/sprites       |
| 叫声仓库               | https://github.com/PokeAPI/cries         |
| Bulbapedia         | https://bulbapedia.bulbagarden.net/      |
