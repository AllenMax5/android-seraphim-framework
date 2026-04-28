package com.seraphim.app.yxsg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.app.yxsg.ui.theme.NotCheckedInColor

/**
 * 日历日期状态指示器 - 改进版
 *
 * 使用双点系统，独立展示午餐和晚餐的签到状态：
 * - 左点 = 午餐状态（绿色）
 * - 右点 = 晚餐状态（橙色）
 * - 实心 = 已签到
 * - 空心/灰色 = 未签到
 *
 * 相比旧版的单点计数方式，用户可以一眼看出具体哪一餐签到了。
 */
@Composable
fun DayStatusIndicatorV2(
    lunchCheckedIn: Boolean,
    dinnerCheckedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 午餐指示点
        MealDot(
            checkedIn = lunchCheckedIn,
            color = LunchColor,
        )
        // 晚餐指示点
        MealDot(
            checkedIn = dinnerCheckedIn,
            color = DinnerColor,
        )
    }
}

@Composable
private fun MealDot(
    checkedIn: Boolean,
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(5.dp)
            .background(
                color = if (checkedIn) color else NotCheckedInColor,
                shape = CircleShape,
            ),
    )
}

/**
 * 日历日期格背景色 - 根据签到状态返回不同的背景色
 *
 * - 0 次：透明
 * - 1 次：浅色半透明背景
 * - 2 次：更明显的浅色背景
 */
@Composable
fun dayCellBackgroundColor(lunchCheckedIn: Boolean, dinnerCheckedIn: Boolean): Color {
    val count = (if (lunchCheckedIn) 1 else 0) + (if (dinnerCheckedIn) 1 else 0)
    return when (count) {
        2 -> LunchColor.copy(alpha = 0.08f)
        1 -> LunchColor.copy(alpha = 0.04f)
        else -> Color.Transparent
    }
}

/**
 * 签到次数标签 - 在日期下方显示 "午/晚" 文字标记
 */
@Composable
fun MealCheckInLabel(
    lunchCheckedIn: Boolean,
    dinnerCheckedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = "午",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                fontWeight = if (lunchCheckedIn) FontWeight.Bold else FontWeight.Normal,
            ),
            color = if (lunchCheckedIn) LunchColor else NotCheckedInColor,
        )
        Text(
            text = "晚",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                fontWeight = if (dinnerCheckedIn) FontWeight.Bold else FontWeight.Normal,
            ),
            color = if (dinnerCheckedIn) DinnerColor else NotCheckedInColor,
        )
    }
}
