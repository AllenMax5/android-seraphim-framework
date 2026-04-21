package com.seraphim.app.yxsg.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material 3 基准 Type Scale + 中文调优
 *
 * 中文排版关键点：
 * - 标题加粗（Medium/SemiBold/Bold），避免默认字重过细
 * - 增大行高，中文需要比英文更大的行间距
 * - 标签文字增加字间距
 */
private val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(lineHeight = 64.sp),
    displayMedium = baseline.displayMedium.copy(lineHeight = 52.sp),
    displaySmall = baseline.displaySmall.copy(lineHeight = 44.sp),
    headlineLarge = baseline.headlineLarge.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
    ),
    titleLarge = baseline.titleLarge.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
    ),
    titleMedium = baseline.titleMedium.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    ),
    titleSmall = baseline.titleSmall.copy(
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = baseline.bodyLarge.copy(lineHeight = 24.sp),
    bodyMedium = baseline.bodyMedium.copy(lineHeight = 20.sp),
    bodySmall = baseline.bodySmall.copy(lineHeight = 16.sp),
    labelLarge = baseline.labelLarge.copy(fontWeight = FontWeight.Medium),
    labelMedium = baseline.labelMedium.copy(letterSpacing = 0.5.sp),
    labelSmall = baseline.labelSmall.copy(letterSpacing = 0.5.sp),
)
