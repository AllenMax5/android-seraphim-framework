package com.seraphim.app.nfc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 空状态占位组件
 *
 * 用于：
 * - 卡包为空
 * - 搜索结果为空
 * - 未检测到 NFC 标签
 */
@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyWalletState(modifier: Modifier = Modifier) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Filled.Nfc,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        },
        title = "卡包空空如也",
        subtitle = "点击右下角 + 号读取第一张 NFC 卡片",
        modifier = modifier,
    )
}

@Composable
fun EmptySearchResultState(query: String, modifier: Modifier = Modifier) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        },
        title = "未找到「$query」",
        subtitle = "尝试其他关键词或添加新卡片",
        modifier = modifier,
    )
}

@Composable
fun NfcNotDetectedState(modifier: Modifier = Modifier) {
    EmptyState(
        icon = {
            Icon(
                imageVector = Icons.Filled.Nfc,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
            )
        },
        title = "未检测到 NFC 标签",
        subtitle = "请将卡片贴近手机背面 NFC 感应区",
        modifier = modifier,
    )
}
