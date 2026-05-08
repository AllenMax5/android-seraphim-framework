package com.seraphim.app.nfc.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seraphim.app.nfc.ui.theme.*
import com.seraphim.nfc.shared.domain.model.HillCardInfo

@Composable
fun CardItem(
    card: HillCardInfo,
    onClick: () -> Unit,
    onEmulate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupColor = when (card.group) {
        "家" -> CardGreen
        "公司" -> CardOrange
        "访客" -> CardBlue
        else -> CardPurple
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 左侧彩色竖条
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(groupColor, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
            )

            // 内容区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // 卡片图标
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(groupColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = groupColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    // 文字信息
                    Column {
                        Text(
                            text = card.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "UID: ${card.uid.takeLast(8)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = card.cardType.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = groupColor,
                        )
                    }
                }

                // 模拟按钮
                IconButton(
                    onClick = onEmulate,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "模拟",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun GroupTabs(
    groups: List<String>,
    selectedGroup: String,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        selectedTabIndex = groups.indexOf(selectedGroup).coerceAtLeast(0),
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        edgePadding = 16.dp,
    ) {
        groups.forEach { group ->
            val selected = group == selectedGroup
            Tab(
                selected = selected,
                onClick = { onGroupSelected(group) },
                text = {
                    Text(
                        text = group,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
            )
        }
    }
}
