package com.seraphim.app.yxsg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.theme.CheckedInColor
import com.seraphim.app.yxsg.ui.theme.LunchColor

/**
 * 日历日期状态指示器
 *
 * - 0 次: 不显示
 * - 1 次: 空心圆环（outline style）
 * - 2 次: 实心圆
 */
@Composable
fun DayStatusIndicator(
    count: Int,
    modifier: Modifier = Modifier,
) {
    when (count) {
        1 -> Box(
            modifier = modifier
                .size(6.dp)
                .border(2.dp, LunchColor, CircleShape),
        )

        2 -> Box(
            modifier = modifier
                .size(6.dp)
                .background(CheckedInColor, CircleShape),
        )

        else -> Unit
    }
}
