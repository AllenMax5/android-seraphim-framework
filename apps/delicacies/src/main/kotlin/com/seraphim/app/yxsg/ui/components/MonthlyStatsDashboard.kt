package com.seraphim.app.yxsg.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.delicacies.shared.domain.model.MonthlyStats

/**
 * 月度统计进度看板
 *
 * 设计：分段水平进度条 + 数据卡片
 * - 顶部大字显示总完成度百分比
 * - 一条合并进度条：午餐(绿)+晚餐(橙)，直观展示各自占比和总进度
 * - 下方两个卡片并排展示午餐/晚餐各自数据
 */
@Composable
fun MonthlyStatsDashboard(
    stats: MonthlyStats,
    modifier: Modifier = Modifier,
) {
    val totalPercent = if (stats.maxCount > 0) stats.totalCount.toFloat() / stats.maxCount else 0f
    val lunchPercent = if (stats.maxCount > 0) stats.lunchCount.toFloat() / stats.maxCount else 0f
    val dinnerPercent = if (stats.maxCount > 0) stats.dinnerCount.toFloat() / stats.maxCount else 0f

    // 动画
    val animatedPercent = remember { Animatable(0f) }
    LaunchedEffect(totalPercent) {
        animatedPercent.animateTo(
            targetValue = totalPercent,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }

    val a11yDescription = "本月已签到 ${stats.totalCount} 次，目标 ${stats.maxCount} 次，" +
            "完成度 ${(totalPercent * 100).toInt()}%，其中午餐 ${stats.lunchCount} 次，晚餐 ${stats.dinnerCount} 次"

    Column(
        modifier = modifier
            .semantics { contentDescription = a11yDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 顶部百分比大数字
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "${(animatedPercent.value * 100).toInt()}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "%",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Text(
            text = "${stats.totalCount} / ${stats.maxCount} 次",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 分段进度条
        SegmentedProgressBar(
            lunchPercent = lunchPercent,
            dinnerPercent = dinnerPercent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 进度条图例
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LegendDot(color = LunchColor, label = "午餐 ${stats.lunchCount}次")
            LegendDot(color = DinnerColor, label = "晚餐 ${stats.dinnerCount}次")
            LegendDot(
                color = MaterialTheme.colorScheme.surfaceVariant,
                label = "剩余 ${stats.maxCount - stats.totalCount}次",
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 鼓励语
        Text(
            text = getEncouragementText(totalPercent),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                totalPercent >= 1f -> LunchColor
                totalPercent >= 0.5f -> DinnerColor
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 两个数据卡片并排
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            MealStatCard(
                mealName = "午餐",
                count = stats.lunchCount,
                maxCount = stats.maxCount,
                color = LunchColor,
                modifier = Modifier.weight(1f),
            )
            MealStatCard(
                mealName = "晚餐",
                count = stats.dinnerCount,
                maxCount = stats.maxCount,
                color = DinnerColor,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * 分段进度条：午餐(绿) + 晚餐(橙) + 剩余(背景色)
 */
@Composable
private fun SegmentedProgressBar(
    lunchPercent: Float,
    dinnerPercent: Float,
    modifier: Modifier = Modifier,
) {
    val totalPercent = (lunchPercent + dinnerPercent).coerceAtMost(1f)
    val lunchRatio = if (totalPercent > 0) lunchPercent / totalPercent else 0f
    val dinnerRatio = if (totalPercent > 0) dinnerPercent / totalPercent else 0f

    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(modifier = Modifier.fillMaxHeight()) {
            // 午餐段
            if (lunchPercent > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(lunchPercent)
                        .background(LunchColor),
                )
            }
            // 晚餐段
            if (dinnerPercent > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(dinnerPercent)
                        .background(DinnerColor),
                )
            }
        }
    }
}

/**
 * 餐次数据卡片
 */
@Composable
private fun MealStatCard(
    mealName: String,
    count: Int,
    maxCount: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val percent = if (maxCount > 0) count.toFloat() / maxCount else 0f

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f),
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 餐次名 + 小圆点
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, RoundedCornerShape(2.dp)),
                )
                Text(
                    text = mealName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 次数
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = color,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 小进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percent.coerceAtMost(1f))
                        .background(color),
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${(percent * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LegendDot(
    color: Color,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(2.dp)),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun getEncouragementText(percent: Float): String = when {
    percent >= 1f -> "本月目标达成，太棒了 ✨"
    percent >= 0.75f -> "即将达标，继续加油 💪"
    percent >= 0.5f -> "已经完成一半，不错 🎉"
    percent >= 0.25f -> "继续保持，养成好习惯 🍀"
    percent > 0f -> "开始行动了，坚持就是胜利 👍"
    else -> "本月尚未签到，去签个到吧"
}
