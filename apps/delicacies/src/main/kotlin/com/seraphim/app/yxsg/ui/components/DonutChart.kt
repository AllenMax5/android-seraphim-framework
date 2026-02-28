package com.seraphim.app.yxsg.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.app.yxsg.ui.theme.NotCheckedInColor
import com.seraphim.delicacies.shared.domain.model.MonthlyStats

@Composable
fun DonutChart(
    stats: MonthlyStats,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 24.dp,
) {
    val lunchAngle by animateFloatAsState(
        targetValue = if (stats.maxCount > 0) (stats.lunchCount.toFloat() / stats.maxCount) * 360f else 0f,
        animationSpec = tween(800),
        label = "lunchAngle",
    )
    val dinnerAngle by animateFloatAsState(
        targetValue = if (stats.maxCount > 0) (stats.dinnerCount.toFloat() / stats.maxCount) * 360f else 0f,
        animationSpec = tween(800),
        label = "dinnerAngle",
    )

    Column(
        modifier = modifier,
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

                // Background ring
                drawArc(
                    color = NotCheckedInColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )

                // Lunch arc
                if (lunchAngle > 0f) {
                    drawArc(
                        color = LunchColor,
                        startAngle = -90f,
                        sweepAngle = lunchAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }

                // Dinner arc
                if (dinnerAngle > 0f) {
                    drawArc(
                        color = DinnerColor,
                        startAngle = -90f + lunchAngle,
                        sweepAngle = dinnerAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
            }

            // Center text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${stats.totalCount}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "/ ${stats.maxCount} 次",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendItem(color = LunchColor, label = "午餐 ${stats.lunchCount}")
            Spacer(modifier = Modifier.width(24.dp))
            LegendItem(color = DinnerColor, label = "晚餐 ${stats.dinnerCount}")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
