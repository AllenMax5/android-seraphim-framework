package com.seraphim.app.nfc.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seraphim.nfc.shared.domain.model.BlockData
import com.seraphim.nfc.shared.domain.model.CardType
import com.seraphim.nfc.shared.domain.model.HillCardInfo
import com.seraphim.nfc.shared.domain.model.SectorData

/**
 * 卡片详情页
 *
 * 展示：
 * - UID 大字体
 * - 卡片名称、类型、读取时间
 * - 扇区数据（可折叠，密钥默认隐藏）
 * - 操作按钮：模拟、编辑、导出、删除
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: HillCardInfo,
    onNavigateBack: () -> Unit,
    onEmulate: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onExport: (String) -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showKeyDialog by remember { mutableStateOf(false) }
    var showKeys by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("卡片详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(card.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "编辑")
                    }
                    IconButton(onClick = { onExport(card.id) }) {
                        Icon(Icons.Filled.Share, contentDescription = "导出")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "删除")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // 卡片信息头
            CardInfoHeader(card = card)

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // 扇区数据
            SectorDataSection(
                sectors = card.sectors,
                showKeys = showKeys,
                onToggleKeys = { showKeys = !showKeys },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // 附加信息
            AdditionalInfoSection(card = card)

            Spacer(modifier = Modifier.height(16.dp))

            // 底部操作按钮
            ActionButtons(
                onEmulate = { onEmulate(card.id) },
                onEdit = { onEdit(card.id) },
                onExport = { onExport(card.id) },
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // 删除确认对话框
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("删除后无法恢复，是否确认删除「${card.name}」？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(card.id)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun CardInfoHeader(card: HillCardInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // UID 大字体
        Text(
            text = card.uid,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 卡片名称
        Text(
            text = card.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 类型标签
        AssistChip(
            onClick = { },
            label = { Text(card.cardType.name) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = when (card.cardType) {
                    CardType.MIFARE_CLASSIC_1K -> MaterialTheme.colorScheme.primaryContainer
                    CardType.MIFARE_CLASSIC_4K -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
            )
        )
    }
}

@Composable
private fun SectorDataSection(
    sectors: List<SectorData>,
    showKeys: Boolean,
    onToggleKeys: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "扇区数据 (${sectors.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            if (sectors.any { it.keyA != null || it.keyB != null }) {
                TextButton(onClick = onToggleKeys) {
                    Text(if (showKeys) "隐藏密钥" else "显示密钥")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        sectors.forEach { sector ->
            SectorItem(
                sector = sector,
                showKeys = showKeys,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectorItem(
    sector: SectorData,
    showKeys: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 扇区标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "扇区 ${sector.sectorIndex}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "收起" else "展开")
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // 块数据
                sector.blocks.forEach { block ->
                    BlockRow(block = block)
                }

                // 密钥信息（可选显示）
                if (showKeys && (sector.keyA != null || sector.keyB != null)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "密钥信息",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    sector.keyA?.let {
                        KeyRow(label = "Key A", value = it)
                    }
                    sector.keyB?.let {
                        KeyRow(label = "Key B", value = it)
                    }
                    sector.accessBits?.let {
                        KeyRow(label = "访问位", value = it)
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockRow(block: BlockData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "块 ${block.blockIndex}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(56.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = block.data,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
        )
        if (block.isTrailer) {
            Text(
                text = "[尾块]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun KeyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun AdditionalInfoSection(card: HillCardInfo) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "信息",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        InfoRow(label = "读取时间", value = card.readAt.toString())
        InfoRow(label = "厂商", value = card.manufacturer)
        InfoRow(label = "分组", value = card.group)
        if (card.note.isNotBlank()) {
            InfoRow(label = "备注", value = card.note)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ActionButtons(
    onEmulate: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        // 主按钮：模拟
        Button(
            onClick = onEmulate,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(Icons.Filled.Smartphone, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("模拟此卡")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 次要操作
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = onExport,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("导出")
            }
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("编辑")
            }
        }
    }
}
