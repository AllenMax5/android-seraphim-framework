package com.seraphim.app.nfc.ui.emulate

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seraphim.nfc.shared.domain.model.HillCardInfo

/**
 * 模拟模式页面
 *
 * 状态：
 * - 未选择卡片：提示选择
 * - 已选择，未激活："靠近读卡器" 引导
 * - 激活中：脉冲动画 + 状态日志
 * - 成功/失败：对应提示
 */
@Composable
fun EmulateScreen(
    card: HillCardInfo?,
    isActive: Boolean,
    statusLog: List<String>,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    onSelectCard: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        if (card == null) {
            // 未选择卡片
            NoCardSelected(onSelectCard = onSelectCard)
        } else {
            // 已选择卡片
            CardPreview(card = card)

            Spacer(modifier = Modifier.height(24.dp))

            // 状态指示器
            EmulateStatusIndicator(isActive = isActive)

            Spacer(modifier = Modifier.height(24.dp))

            // 激活/停用按钮
            if (isActive) {
                Button(
                    onClick = onDeactivate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("停止模拟")
                }
            } else {
                Button(
                    onClick = onActivate,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.Nfc, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("开始模拟")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 状态日志
            if (statusLog.isNotEmpty()) {
                StatusLog(logs = statusLog)
            }
        }
    }
}

@Composable
private fun NoCardSelected(onSelectCard: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "未选择卡片",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "请先从卡包中选择一张卡片进行模拟",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSelectCard) {
            Text("去选择卡片")
        }
    }
}

@Composable
private fun CardPreview(card: HillCardInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = card.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.uid,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.cardType.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun EmulateStatusIndicator(isActive: Boolean) {
    if (isActive) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 脉冲动画指示器
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(120.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Nfc,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "模拟中... 请靠近读卡器",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        Text(
            text = "点击开始模拟",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatusLog(logs: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "状态日志",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            logs.takeLast(5).forEach { log ->
                Text(
                    text = log,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                )
            }
        }
    }
}
