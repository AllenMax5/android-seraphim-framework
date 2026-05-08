package com.seraphim.app.nfc.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.seraphim.app.nfc.ui.home.NfcWaveState
import com.seraphim.app.nfc.ui.theme.KeyWorldPrimary
import com.seraphim.app.nfc.ui.theme.SuccessColor
import com.seraphim.app.nfc.ui.theme.ErrorColor

/**
 * NFC 波纹动画组件
 *
 * 根据状态显示不同的动画效果：
 * - Idle: 缓慢呼吸的灰色圆环
 * - Scanning: 青色快速扩散波纹
 * - Success: 绿色扩散 + 对勾
 * - Error: 红色闪烁
 */
@Composable
fun NfcWaveAnimation(
    state: NfcWaveState,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_wave")

    // 呼吸动画
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )

    // 扫描波纹
    val waveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave",
    )

    // 成功动画
    val successScale by animateFloatAsState(
        targetValue = if (state == NfcWaveState.Success) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "success",
    )

    val baseColor = when (state) {
        NfcWaveState.Idle -> MaterialTheme.colorScheme.outline
        NfcWaveState.Scanning -> KeyWorldPrimary
        NfcWaveState.Success -> SuccessColor
        NfcWaveState.Error -> ErrorColor
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2 * 0.9f

            when (state) {
                NfcWaveState.Idle -> {
                    // 呼吸圆环
                    val radius = maxRadius * breathScale
                    drawCircle(
                        color = baseColor.copy(alpha = 0.3f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                    )
                    // 内部小圆
                    drawCircle(
                        color = baseColor.copy(alpha = 0.1f),
                        radius = radius * 0.5f,
                        center = center,
                    )
                }

                NfcWaveState.Scanning -> {
                    // 多层扩散波纹
                    repeat(3) { index ->
                        val progress = (waveProgress + index * 0.33f) % 1f
                        val alpha = 1f - progress
                        val radius = maxRadius * progress
                        drawCircle(
                            color = baseColor.copy(alpha = alpha * 0.5f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 2.dp.toPx()),
                        )
                    }
                    // 中心实心
                    drawCircle(
                        color = baseColor.copy(alpha = 0.2f),
                        radius = maxRadius * 0.3f,
                        center = center,
                    )
                }

                NfcWaveState.Success -> {
                    // 成功扩散
                    val radius = maxRadius * successScale
                    drawCircle(
                        color = baseColor.copy(alpha = 0.2f),
                        radius = radius,
                        center = center,
                    )
                    drawCircle(
                        color = baseColor,
                        radius = maxRadius * 0.35f,
                        center = center,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                    )
                    // 中心对勾用 Compose Icon 绘制更好，这里画个圆点代替
                    drawCircle(
                        color = baseColor,
                        radius = maxRadius * 0.15f,
                        center = center,
                    )
                }

                NfcWaveState.Error -> {
                    // 错误闪烁
                    val flickerAlpha = if (waveProgress > 0.5f) 0.8f else 0.3f
                    drawCircle(
                        color = baseColor.copy(alpha = flickerAlpha),
                        radius = maxRadius * 0.8f,
                        center = center,
                        style = Stroke(width = 3.dp.toPx()),
                    )
                    drawCircle(
                        color = baseColor.copy(alpha = 0.1f),
                        radius = maxRadius * 0.4f,
                        center = center,
                    )
                }
            }
        }
    }
}
