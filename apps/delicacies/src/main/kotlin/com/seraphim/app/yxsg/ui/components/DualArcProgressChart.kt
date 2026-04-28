package com.seraphim.app.yxsg.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.app.yxsg.ui.theme.NotCheckedInColor
import com.seraphim.delicacies.shared.domain.model.MonthlyStats

/**
 * 双半圆环进度图表
 *
 * 将圆环分为左右两个半圆：
 * - 左半圆：午餐进度（绿色）
 * - 右半圆：晚餐进度（橙色）
 *
 * 中心显示总进度百分比和次数
 * 视觉上像两个独立的仪表盘并排，更直观地展示午/晚餐各自的完成度
 */
@Composable
fun DualArcProgressChart(
    stats: MonthlyStats,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    strokeWidth: Dp = 20.dp,
) {
    // 计算百分比
    val lunchPercent = if (stats.maxCount > 0) stats.lunchCount.toFloat() / stats.maxCount else 0f
    val dinnerPercent = if (stats.maxCount > 0) stats.dinnerCount.toFloat() / stats.maxCount else 0f
    val totalPercent = if (stats.maxCount > 0) stats.totalCount.toFloat() / stats.maxCount else 0f

    // 动画值
    val lunchSweep = remember { Animatable(0f) }
    val dinnerSweep = remember { Animatable(0f) }
    val totalCountAnim = remember { Animatable(0f) }

    LaunchedEffect(stats.lunchCount, stats.dinnerCount, stats.totalCount) {
        lunchSweep.animateTo(
            targetValue = lunchPercent * 180f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
        )
        dinnerSweep.animateTo(
            targetValue = dinnerPercent * 180f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
        )
        totalCountAnim.animateTo(
            targetValue = stats.totalCount.toFloat(),
            animationSpec = tween(800),
        )
    }

    val a11yDescription = "本月已签到 ${stats.totalCount} 次，目标 ${stats.maxCount} 次，" +
            "完成度 ${(totalPercent * 100).toInt()}%，其中午餐 ${stats.lunchCount} 次，晚餐 ${stats.dinnerCount} 次"

    Column(
        modifier = modifier.semantics { contentDescription = a11yDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = strokeWidth.toPx()
                val diameter = this.size.minDimension - stroke
                val topLeft = Offset(
                    (this.size.width - diameter) / 2f,
                    (this.size.height - diameter) / 2f,
                )
                val arcSize = Size(diameter, diameter)
                val centerY = this.size.height / 2f

                // 背景环 - 左半圆（午餐）
                drawArc(
                    color = NotCheckedInColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )

                // 背景环 - 右半圆（晚餐）
                drawArc(
                    color = NotCheckedInColor,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )

                // 午餐进度弧（左半圆，从底部逆时针）
                if (lunchSweep.value > 0f) {
                    drawArc(
                        color = LunchColor,
                        startAngle = 180f,
                        sweepAngle = lunchSweep.value,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }

                // 晚餐进度弧（右半圆，从顶部顺时针）
                if (dinnerSweep.value > 0f) {
                    drawArc(
                        color = DinnerColor,
                        startAngle = 0f,
                        sweepAngle = dinnerSweep.value,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }

                // 中间分隔线（小圆点装饰）
                val dotRadius = stroke * 0.3f
                drawCircle(
                    color = LunchColor,
                    radius = dotRadius,
                    center = Offset(this.size.width / 2f - diameter / 2f - stroke / 2f, centerY),
                )
                drawCircle(
                    color = DinnerColor,
                    radius = dotRadius,
                    center = Offset(this.size.width / 2f + diameter / 2f + stroke / 2f, centerY),
                )
            }

            // 中心内容
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 百分比大数字
                Text(
                    text = "${(totalPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                // 次数
                Text(
                    text = "${totalCountAnim.value.toInt()} / ${stats.maxCount} 次",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                // 鼓励语
                Text(
                    text = getEncouragementText(totalPercent),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (totalPercent >= 1f) {
                        LunchColor
                    } else if (totalPercent >= 0.5f) {
                        DinnerColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 图例 - 左右并排
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize(),
        ) {
            LegendItem(
                color = LunchColor,
                label = "午餐",
                count = stats.lunchCount,
                percent = (lunchPercent * 100).toInt(),
            )
            LegendItem(
                color = DinnerColor,
                label = "晚餐",
                count = stats.dinnerCount,
                percent = (dinnerPercent * 100).toInt(),
            )
        }
    }
}

/**
 * 根据完成度返回鼓励文案
 */
private fun getEncouragementText(percent: Float): String = when {
    percent >= 1f -> "本月目标达成 ✨"
    percent >= 0.75f -> "即将达标，加油 💪"
    percent >= 0.5f -> "已经完成一半 🎉"
    percent >= 0.25f -> "继续保持 🍀"
    percent > 0f -> "开始行动了 👍"
    else -> "本月尚未签到"
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    count: Int,
    percent: Int,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = "$label $count 次",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
