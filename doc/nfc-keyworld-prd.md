# NFC门禁卡管家 — Hill门禁卡模拟器 PRD

> **版本**: v1.0.0  
> **最后更新**: 2026-04-28  
> **模块**: `apps/nfc` · `shareds/nfc`  
> **包名**: `com.seraphim.app.nfc`
> **应用名**: **钥界** (KeyWorld)

---

## 1. 产品概述

### 1.1 产品定位

**钥界 (KeyWorld)** 是一款专注于 **Hill 门禁卡管理** 的 NFC 工具应用。核心功能包括：读取门禁卡信息、模拟门禁卡、管理多张门禁卡。应用采用深色主题为主的设计风格，强调科技感与安全感，所有卡片数据本地加密存储，无需联网。

### 1.2 目标用户

- 居住在配备 Hill 门禁系统小区的住户
- 需要管理多张门禁卡（主卡、副卡、访客卡）的用户
- 希望用手机替代实体门禁卡的人群

### 1.3 核心价值

| 价值点 | 说明 |
|--------|------|
| 读卡识信息 | 将实体门禁卡贴靠手机，即刻读取卡号、类型、有效期等完整信息 |
| 手机模拟 | 支持将门禁卡信息写入手机 NFC，刷手机即可开门 |
| 多卡管理 | 一个 App 管理全家门禁卡，支持分组、命名、备注 |
| 安全加密 | 所有卡片数据 AES-256 本地加密，支持指纹/面容解锁 |
| 离线使用 | 模拟后的卡片无需联网，手机没电前都能用 |

---

## 2. 功能需求

### 2.1 读卡功能（P0 — 核心）

#### 2.1.1 读卡页面（首页）

- **NFC 状态检测**
  - 打开页面时自动检测手机 NFC 是否开启
  - NFC 关闭时显示引导开启的浮层，提供一键跳转系统设置按钮
  - 顶部常驻 NFC 信号强度指示器（动画波纹效果）

- **读卡交互区域**
  - 中央放置大型 NFC 感应动画（脉冲波纹扩散效果）
  - 文案提示："将门禁卡贴靠手机背面" / "正在读取..." / "读取成功"
  - 读卡成功时触发成功动效（卡片翻转动画 + 轻微震动反馈）

- **读取信息展示**
  - 卡号（UID）
  - 卡片类型（MIFARE Classic 1K / 4K / DESFire / Hill 专用卡）
  - 扇区数据（可展开查看 16 个扇区的 HEX 数据）
  - 厂商信息
  - 读取时间

- **读取后操作**
  - 「保存到卡包」→ 输入卡片名称（如"家门主卡"）、选择分组、添加备注
  - 「直接模拟」→ 跳过保存直接尝试模拟
  - 「分享卡片」→ 生成加密二维码（供其他钥界用户导入）

#### 2.1.2 读卡数据模型

```
HillCardInfo {
    id: String (UUID, 本地主键)
    uid: String           // 卡号，如 "A1:B2:C3:D4"
    cardType: CardType    // MIFARE_CLASSIC_1K / MIFARE_CLASSIC_4K / DESFIRE / HILL_PROPRIETARY
    sectors: List<SectorData>  // 扇区数据
    manufacturer: String   // 厂商信息
    readAt: LocalDateTime // 读取时间
    name: String          // 用户自定义名称
    group: String         // 分组（默认/家/公司/访客）
    note: String          // 备注
    isEncrypted: Boolean  // 是否已加密存储
    createdAt: LocalDateTime
    updatedAt: LocalDateTime
}

SectorData {
    sectorIndex: Int      // 扇区索引 0-15
    blocks: List<BlockData>
    keyA: String?         // Key A (HEX)
    keyB: String?         // Key B (HEX)
    accessBits: String?   // 访问控制位
}

BlockData {
    blockIndex: Int       // 块索引
    data: String          // 16字节 HEX 数据
    isTrailer: Boolean    // 是否为尾块（包含密钥）
}
```

### 2.2 卡包管理（P0 — 核心）

#### 2.2.1 卡包列表页面

- **卡片展示**
  - 纵向卡片列表，每张卡片为一个卡片式 Item
  - 卡片视觉设计：左侧彩色竖条（不同分组不同色）+ 卡片图标 + 名称 + UID 后四位
  - 支持折叠/展开查看卡片详细信息

- **分组筛选**
  - 顶部 Tab 栏：全部 / 家 / 公司 / 访客 / 未分组
  - 支持自定义分组（增删改）

- **搜索**
  - 顶部搜索栏，支持按名称、UID、备注搜索

- **卡片操作**
  - 长按弹出操作菜单：模拟 / 编辑 / 删除 / 分享
  - 左滑快捷操作：模拟 / 删除

- **空状态**
  - 未保存任何卡片时显示引导插画 + "去读取第一张门禁卡" 按钮

#### 2.2.2 卡片详情页面

- **信息卡片**
  - 卡片名称（可编辑）
  - UID 大字体展示
  - 类型标签
  - 读取时间

- **扇区数据查看**
  - 可折叠的扇区列表
  - 每个扇区显示 4 个块的 HEX 数据
  - 支持复制单块数据 / 复制整个扇区
  - 密钥区域默认隐藏，点击"显示密钥"后输入主密码查看

- **操作按钮**
  - 「模拟此卡」→ 跳转到模拟页面
  - 「编辑信息」→ 修改名称、分组、备注
  - 「导出卡片」→ 生成加密文件/二维码
  - 「删除卡片」→ 二次确认后删除

### 2.3 模拟功能（P0 — 核心）

#### 2.3.1 模拟页面

- **模拟状态展示**
  - 大型状态指示器：未模拟 / 模拟中 / 模拟成功 / 模拟失败
  - 模拟成功时显示"手机已化身门禁卡，贴靠读卡器即可开门"

- **模拟过程**
  - 选择要模拟的卡片
  - 检测手机是否支持 HCE（Host Card Emulation）
  - 将卡片数据写入 NFC 模拟缓冲区
  - 尝试向读卡器发送认证响应

- **快捷模拟**
  - 支持从卡包一键模拟
  - 支持从读卡结果直接模拟
  - 支持设置"默认卡"，锁屏状态下双击电源键直接唤起默认卡

#### 2.3.2 模拟限制与提示

- 明确告知用户：
  - 仅支持未加密或已知密钥的 MIFARE Classic 卡片
  - 部分加密等级高的 Hill 卡可能无法模拟
  - 模拟需要手机支持 HCE 且系统版本 Android 10+
  - 模拟状态下会消耗额外电量

### 2.4 安全与隐私（P1 — 重要）

#### 2.4.1 应用锁

- 支持系统级生物识别（指纹/面容）解锁应用
- 支持自定义数字密码
- 支持"每次打开"或"退出后 5 分钟"两种锁定策略

#### 2.4.2 数据加密

- 所有卡片扇区数据使用 AES-256-GCM 加密
- 密钥派生使用 PBKDF2（10000 轮）
- 加密密钥由用户主密码 + 设备唯一标识符派生

#### 2.4.3 卡片分享安全

- 分享时生成 AES-128 加密二维码
- 包含过期时间（默认 24 小时）
- 接收方需要钥界 App 扫描导入

### 2.5 设置页面（P2 — 增强）

| 设置项 | 说明 |
|--------|------|
| 应用锁 | 开启/关闭生物识别/密码锁 |
| 主密码 | 修改数据加密主密码 |
| 默认卡 | 设置快捷模拟的默认卡片 |
| NFC 自动读取 | 检测到 NFC 标签时自动读取（无需打开 App） |
| 震动反馈 | 读卡/模拟成功时震动 |
| 数据备份 | 导出加密备份文件到本地/云盘 |
| 数据恢复 | 从备份文件恢复 |
| 清除数据 | 一键清空所有卡片（需主密码确认） |

---

## 3. 非功能需求

### 3.1 性能

- 读卡响应时间 < 500ms（从贴卡到显示结果）
- 卡包列表加载 < 100ms（使用 Room + Flow 响应式更新）
- 模拟切换卡片 < 1s

### 3.2 兼容性

- **最低 SDK**: API 26 (Android 8.0)
- **目标 SDK**: API 35
- **NFC 要求**: 必须支持 NFC 功能（安装时检测，不支持则提示）
- **HCE 要求**: 模拟功能需要 Android 10+ 且支持 HCE

### 3.3 隐私与安全

- 无网络请求，无数据上传
- 所有数据本地加密存储
- 卸载应用即彻底清除所有数据（除非手动导出备份）

---

## 4. 技术架构

### 4.1 技术栈

| 层级 | 技术选型 |
|------|----------|
| UI | Jetpack Compose + Material 3 |
| 导航 | Navigation Compose |
| 状态管理 | ViewModel + StateFlow |
| 依赖注入 | Koin |
| 本地存储 | Room Database + SQLCipher（加密） |
| NFC 操作 | Android NFC API (`android.nfc`) + `NfcAdapter` |
| HCE 模拟 | `HostApduService` |
| 加密 | Android Keystore + AES-256-GCM |
| 生物识别 | BiometricPrompt |
| 动画 | Compose Animation + Lottie |

### 4.2 模块结构

```
apps/nfc/                          ← App 壳模块
├── com.seraphim.app.nfc
│   ├── MainActivity.kt
│   ├── NfcApplication.kt
│   ├── service/
│   │   └── HillCardEmulationService.kt   ← HCE 服务
│   ├── nfc/
│   │   ├── NfcReader.kt                  ← NFC 读取封装
│   │   ├── NfcManager.kt                 ← NFC 状态管理
│   │   └── MifareHelper.kt               ← MIFARE 操作辅助
│   ├── ui/
│   │   ├── home/                         ← 读卡首页
│   │   ├── wallet/                       ← 卡包管理
│   │   ├── detail/                       ← 卡片详情
│   │   ├── emulate/                      ← 模拟页面
│   │   ├── settings/                     ← 设置页面
│   │   └── components/                   ← 共享组件
│   │       ├── NfcWaveAnimation.kt        ← NFC 波纹动画
│   │       ├── CardItem.kt               ← 卡片列表项
│   │       ├── SectorDataView.kt         ← 扇区数据展示
│   │       └── SecurityLock.kt           ← 安全锁组件
│   └── di/
│
shareds/nfc/                       ← KMP 共享模块
├── commonMain/
│   ├── data/
│   │   ├── db/
│   │   │   ├── entity/                 ← Room Entity
│   │   │   ├── dao/
│   │   │   └── database/
│   │   └── repository/
│   ├── domain/
│   │   ├── model/                      ← HillCardInfo, SectorData 等
│   │   ├── usecase/
│   │   └── crypto/                     ← 加密/解密逻辑
│   └── di/
```

### 4.3 数据库设计

#### `hill_cards` 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | TEXT | PK | UUID |
| `uid` | TEXT | NOT NULL | 卡号 |
| `card_type` | TEXT | NOT NULL | 卡片类型 |
| `name` | TEXT | NOT NULL | 用户命名 |
| `group_id` | TEXT | FK | 分组ID |
| `note` | TEXT | | 备注 |
| `sectors_json` | TEXT | NOT NULL | 扇区数据（加密 JSON） |
| `manufacturer` | TEXT | | 厂商 |
| `read_at` | TEXT | NOT NULL | 读取时间 |
| `created_at` | TEXT | NOT NULL | 创建时间 |
| `updated_at` | TEXT | NOT NULL | 更新时间 |

#### `card_groups` 表

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | TEXT | PK | UUID |
| `name` | TEXT | NOT NULL | 分组名称 |
| `color` | TEXT | NOT NULL | 颜色代码 |
| `sort_order` | INTEGER | | 排序 |

---

## 5. 页面交互设计

### 5.1 读卡首页

```
┌──────────────────────────────────┐
│  ≡ 钥界                    🔒 ⚙️   │  ← 顶部栏（菜单/锁定状态/设置）
├──────────────────────────────────┤
│                                  │
│         ╭──────────╮              │
│        ╱            ╲             │  ← NFC 波纹动画（中央大圆形）
│       │   📶 NFC   │             │     未检测：灰色脉冲
│        ╲            ╱             │     检测中：蓝色快速波纹
│         ╰──────────╯              │     成功：绿色扩散 + ✓
│                                  │     失败：红色闪烁 + ✗
│     将门禁卡贴靠手机背面           │
│                                  │
├──────────────────────────────────┤
│  📋 最近读取                      │
│  ┌────────────────────────────┐ │
│  │ 🏠 家门主卡    A1:B2:C3:D4 │ │  ← 最近读取的卡片快捷入口
│  │    今天 14:32  已保存 ✓     │ │
│  └────────────────────────────┘ │
├──────────────────────────────────┤
│  🗂️ 卡包      📟 模拟      ⚙️ 设置 │  ← 底部导航
└──────────────────────────────────┘
```

### 5.2 卡包列表

```
┌──────────────────────────────────┐
│  🗂️ 我的卡包              🔍 +   │
├──────────────────────────────────┤
│  [全部] [家] [公司] [访客] [+]  │  ← 分组 Tab
├──────────────────────────────────┤
│  ┌────────────────────────────┐  │
│  │ 🟢 家门主卡                │  │  ← 卡片项：左侧色条+图标
│  │    UID: ****:****:C3:D4   │  │     展开显示完整 UID + 操作按钮
│  │    MIFARE Classic 1K       │  │
│  │    [模拟] [编辑] [删除]    │  │
│  └────────────────────────────┘  │
│  ┌────────────────────────────┐  │
│  │ 🟠 公司门禁                │  │
│  │    UID: ****:****:E5:F6    │  │
│  └────────────────────────────┘  │
│  ┌────────────────────────────┐  │
│  │ 🔵 访客卡-张三             │  │
│  │    UID: ****:****:A1:B2    │  │
│  │    有效期至 2026-05-01     │  │
│  └────────────────────────────┘  │
├──────────────────────────────────┤
│  📟 模拟      🗂️ 卡包      ⚙️ 设置 │
└──────────────────────────────────┘
```

### 5.3 卡片详情

```
┌──────────────────────────────────┐
│  ← 卡片详情                 ⋮     │
├──────────────────────────────────┤
│  ┌────────────────────────────┐  │
│  │                            │  │
│  │      📟 A1:B2:C3:D4       │  │  ← UID 大字体居中
│  │      家门主卡              │  │  ← 卡片名称
│  │   MIFARE Classic 1K       │  │  ← 类型标签
│  │                            │  │
│  └────────────────────────────┘  │
├──────────────────────────────────┤
│  📊 扇区数据 (16)      [展开 ▼] │
├──────────────────────────────────┤
│  扇区 0                          │
│  ├─ 块 0: A1 B2 C3 D4 ...       │
│  ├─ 块 1: 00 00 00 00 ...       │
│  ├─ 块 2: FF FF FF FF ...       │
│  └─ 块 3 (密钥): ** ** ** **    │  ← 密钥默认隐藏
├──────────────────────────────────┤
│  ℹ️ 信息                         │
│  读取时间: 2026-04-28 14:32      │
│  厂商: NXP Semiconductors        │
│  分组: 家                        │
│  备注: 主门+电梯通用              │
├──────────────────────────────────┤
│     [    📟 模拟此卡    ]        │  ← 底部主按钮
│     [   📤 导出  ] [ ✏️ 编辑 ]   │  ← 次要操作
└──────────────────────────────────┘
```

### 5.4 模拟页面

```
┌──────────────────────────────────┐
│  ← 模拟门禁卡                   │
├──────────────────────────────────┤
│                                  │
│    ┌────────────────────────┐    │
│    │                        │    │
│    │     📟 正在模拟        │    │  ← 状态卡片
│    │                        │    │
│    │   家门主卡             │    │
│    │   A1:B2:C3:D4         │    │
│    │                        │    │
│    │   [ 绿色脉冲动画 ]      │    │
│    │                        │    │
│    │   手机已化身门禁卡      │    │
│    │   贴靠读卡器即可开门    │    │
│    │                        │    │
│    └────────────────────────┘    │
│                                  │
├──────────────────────────────────┤
│  ⚠️ 注意事项                      │
│  • 模拟期间请勿切换其他 NFC 应用  │
│  • 部分加密卡可能无法成功         │
│  • 模拟会略微增加电量消耗         │
├──────────────────────────────────┤
│     [      停止模拟      ]       │
└──────────────────────────────────┘
```

---

## 6. 导航结构

```
NavHost
├── HomeScreen           (route: "home")          ← 默认首页（读卡）
├── WalletScreen         (route: "wallet")         ← 卡包列表
│   └── CardDetailScreen (route: "card/{id}")
├── EmulateScreen        (route: "emulate/{id}?")  ← 模拟页面
├── SettingsScreen       (route: "settings")
└── SecurityLockScreen   (route: "lock")           ← 应用锁
```

使用 **Bottom Navigation Bar** 在 Home / Wallet / Settings 间切换。

---

## 7. NFC 技术实现方案

### 7.1 读卡流程

```kotlin
// NfcReader.kt
class NfcReader(private val adapter: NfcAdapter) {
    
    fun readCard(tag: Tag): HillCardInfo? {
        val mifare = MifareClassic.get(tag) ?: return null
        
        mifare.connect()
        
        val type = when (mifare.type) {
            MifareClassic.TYPE_CLASSIC -> CardType.MIFARE_CLASSIC_1K
            MifareClassic.TYPE_PLUS -> CardType.MIFARE_CLASSIC_4K
            else -> CardType.UNKNOWN
        }
        
        val sectors = mutableListOf<SectorData>()
        val sectorCount = mifare.sectorCount
        
        // 尝试使用默认密钥读取所有扇区
        val defaultKeys = listOf(
            MifareClassic.KEY_DEFAULT,
            MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY,
            MifareClassic.KEY_NFC_FORUM
        )
        
        for (sectorIndex in 0 until sectorCount) {
            var sectorRead = false
            
            for (key in defaultKeys) {
                if (mifare.authenticateSectorWithKeyA(sectorIndex, key)) {
                    val blocks = mutableListOf<BlockData>()
                    val blockCount = mifare.getBlockCountInSector(sectorIndex)
                    val firstBlock = mifare.sectorToBlock(sectorIndex)
                    
                    for (blockOffset in 0 until blockCount) {
                        val blockIndex = firstBlock + blockOffset
                        val data = mifare.readBlock(blockIndex)
                        blocks.add(BlockData(
                            blockIndex = blockIndex,
                            data = data.toHex(),
                            isTrailer = blockOffset == blockCount - 1
                        ))
                    }
                    
                    sectors.add(SectorData(
                        sectorIndex = sectorIndex,
                        blocks = blocks,
                        keyA = key.toHex()
                    ))
                    sectorRead = true
                    break
                }
            }
            
            if (!sectorRead) {
                // 记录无法读取的扇区
                sectors.add(SectorData(
                    sectorIndex = sectorIndex,
                    blocks = emptyList(),
                    keyA = null
                ))
            }
        }
        
        mifare.close()
        
        return HillCardInfo(
            uid = tag.id.toHex(),
            cardType = type,
            sectors = sectors,
            manufacturer = tag.techList.joinToString(),
            readAt = Clock.System.now()
        )
    }
}
```

### 7.2 HCE 模拟服务

```kotlin
// HillCardEmulationService.kt
class HillCardEmulationService : HostApduService() {
    
    private var activeCard: HillCardInfo? = null
    
    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        val card = activeCard ?: return SW_INS_NOT_SUPPORTED
        
        // 解析 APDU 命令
        val cla = commandApdu[0]
        val ins = commandApdu[1]
        
        return when (ins) {
            INS_SELECT -> handleSelect(commandApdu, card)
            INS_AUTH -> handleAuth(commandApdu, card)
            INS_READ -> handleRead(commandApdu, card)
            else -> SW_INS_NOT_SUPPORTED
        }
    }
    
    override fun onDeactivated(reason: Int) {
        // 模拟被停用（手机离开读卡器或切换应用）
    }
    
    fun setActiveCard(card: HillCardInfo) {
        activeCard = card
    }
    
    companion object {
        val SW_SUCCESS = byteArrayOf(0x90.toByte(), 0x00.toByte())
        val SW_INS_NOT_SUPPORTED = byteArrayOf(0x6D.toByte(), 0x00.toByte())
        
        const val INS_SELECT = 0xA4.toByte()
        const val INS_AUTH = 0x60.toByte()
        const val INS_READ = 0x30.toByte()
    }
}
```

### 7.3 AndroidManifest 声明

```xml
<manifest>
    <!-- NFC 权限 -->
    <uses-permission android:name="android.permission.NFC" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    
    <!-- NFC 功能声明 -->
    <uses-feature android:name="android.hardware.nfc" android:required="true" />
    <uses-feature android:name="android.hardware.nfc.hce" android:required="true" />
    
    <application>
        <!-- HCE 服务 -->
        <service
            android:name=".service.HillCardEmulationService"
            android:exported="true"
            android:permission="android.permission.BIND_NFC_SERVICE">
            <intent-filter>
                <action android:name="android.nfc.cardemulation.action.HOST_APDU_SERVICE" />
            </intent-filter>
            <meta-data
                android:name="android.nfc.cardemulation.host_apdu_service"
                android:resource="@xml/apduservice" />
        </service>
        
        <!-- NFC 前台分发 -->
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.nfc.action.TECH_DISCOVERED" />
            </intent-filter>
            <meta-data
                android:name="android.nfc.action.TECH_DISCOVERED"
                android:resource="@xml/nfc_tech_filter" />
        </activity>
    </application>
</manifest>
```

---

## 8. 视觉设计规范

### 8.1 主题色

```kotlin
// Color.kt
val KeyWorldPrimary = Color(0xFF00BCD4)        // 青色 - 科技感
val KeyWorldSecondary = Color(0xFF7C4DFF)    // 深紫 - 神秘感
val KeyWorldTertiary = Color(0xFF00E5FF)       // 亮青 - 高亮

val CardGreen = Color(0xFF4CAF50)              // 家分组
val CardOrange = Color(0xFFFF9800)             // 公司分组
val CardBlue = Color(0xFF2196F3)               // 访客分组
val CardPurple = Color(0xFF9C27B0)             // 未分组

val DarkBackground = Color(0xFF121212)         // 深色背景
val DarkSurface = Color(0xFF1E1E1E)            // 卡片背景
val DarkElevated = Color(0xFF2C2C2C)           //  elevated 表面
```

### 8.2 图标资源

| 用途 | 图标 | 来源 |
|------|------|------|
| App Icon | 钥匙 + NFC 波纹 | iconfont.cn 搜索 "钥匙" "NFC" |
| 读卡 | 信用卡 + 波纹 | iconfont.cn 搜索 "读卡" |
| 卡包 | 钱包 / 卡包 | iconfont.cn 搜索 "卡包" |
| 模拟 | 手机 + 信号 | iconfont.cn 搜索 "模拟" "信号" |
| 设置 | 齿轮 | Material Icons |
| 安全 | 盾牌 / 锁 | iconfont.cn 搜索 "安全" |
| 成功 | 对勾圆圈 | Material Icons |
| 失败 | 叉号圆圈 | Material Icons |

### 8.3 动画规范

| 场景 | 动画 | 时长 |
|------|------|------|
| NFC 检测中 | 同心圆波纹扩散 | 无限循环，1.5s/周期 |
| 读卡成功 | 波纹变绿 + 轻微缩放 + 震动 | 0.5s |
| 读卡失败 | 波纹变红 + 左右抖动 | 0.4s |
| 卡片保存 | 卡片从底部滑入列表 | 0.3s |
| 模拟切换 | 卡片翻转 | 0.4s |
| 页面切换 | 共享元素过渡 | 0.3s |

---

## 9. 里程碑 & 排期

| 阶段 | 内容 | 预估工时 |
|------|------|----------|
| M1 — 基础框架 | 模块搭建、NFC 权限、HCE 服务骨架、数据库 | 2 天 |
| M2 — 读卡核心 | NFC 读取封装、MIFARE 解析、扇区数据展示 | 3 天 |
| M3 — 卡包管理 | 卡包列表、分组、搜索、详情页 | 2 天 |
| M4 — 模拟功能 | HCE APDU 交互、模拟状态管理、快捷唤起 | 3 天 |
| M5 — 安全体系 | 生物识别、AES 加密、密钥派生 | 2 天 |
| M6 — UI 打磨 | 动画、主题、图标、空状态、错误处理 | 2 天 |
| M7 — 测试优化 | NFC 真机测试、多卡兼容性、性能优化 | 2 天 |

**总计**: ~16 天

---

## 10. 验收标准

- [ ] 支持读取 MIFARE Classic 1K/4K 卡片的完整扇区数据
- [ ] 支持保存至少 50 张卡片到本地加密数据库
- [ ] 支持分组管理、搜索、编辑卡片信息
- [ ] 支持将已保存卡片模拟为 NFC 标签（HCE）
- [ ] 支持生物识别/密码解锁应用
- [ ] 所有卡片数据 AES-256 加密存储
- [ ] NFC 读卡响应时间 < 500ms
- [ ] 应用在无网络环境下完全可用
- [ ] 支持导出/导入加密备份
- [ ] 适配 Android 8.0 ~ 15 的 NFC 机型

---

## 11. 附录

### 11.1 参考资源

- [Android NFC 官方文档](https://developer.android.com/guide/topics/connectivity/nfc)
- [Host-based Card Emulation](https://developer.android.com/guide/topics/connectivity/nfc/hce)
- [MIFARE Classic 协议文档](https://www.nxp.com/docs/en/data-sheet/MF1S50YYX_V1.pdf)
- [iconfont.cn — 图标资源](https://www.iconfont.cn/)

### 11.2 名词解释

| 术语 | 说明 |
|------|------|
| NFC | Near Field Communication，近场通信 |
| HCE | Host Card Emulation，主机卡模拟 |
| APDU | Application Protocol Data Unit，智能卡通信协议数据单元 |
| MIFARE | NXP 公司的 RFID 芯片系列产品 |
| UID | Unique Identifier，卡片的唯一标识号 |
| 扇区 (Sector) | MIFARE Classic 卡片的存储分区，1K 卡有 16 个扇区 |
| 块 (Block) | 扇区内的存储单元，每个扇区有 4 个块，每块 16 字节 |
| Key A / Key B | MIFARE 扇区的访问密钥，用于认证和数据读写 |
| Hill | 目标门禁系统品牌 |
