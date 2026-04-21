package com.seraphim.app.yxsg.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.delicacies.shared.domain.model.MealType

/**
 * 餐次签到按钮
 *
 * 基于 Material 3 FilledTonalButton，提供清晰的按钮语义、触觉反馈和无障碍支持。
 */
@Composable
fun MealCheckInButton(
    mealType: MealType,
    checkedIn: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val mealColor = mealType.color
    val mealLabel = mealType.label

    val icon = when {
        checkedIn -> Icons.Rounded.CheckCircle
        mealType == MealType.LUNCH -> Icons.Rounded.LunchDining
        else -> Icons.Rounded.DinnerDining
    }

    val statusLabel = when {
        checkedIn -> "已签到"
        !enabled -> "本月已达上限"
        else -> "点击签到"
    }

    FilledTonalButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            onClick()
        },
        enabled = enabled && !checkedIn,
        modifier = modifier
            .height(120.dp)
            .semantics {
                stateDescription = "$mealLabel · $statusLabel"
            },
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (checkedIn) {
                mealColor.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = if (checkedIn) mealColor else MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = if (checkedIn) {
                mealColor.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = mealLabel,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (checkedIn) "$mealLabel · 已签到" else mealLabel,
                style = MaterialTheme.typography.labelLarge,
            )
            if (!checkedIn && enabled) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "点击签到",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** 餐次对应的显示颜色 */
val MealType.color: Color
    get() = when (this) {
        MealType.LUNCH -> LunchColor
        MealType.DINNER -> DinnerColor
    }

/** 餐次对应的显示名称 */
val MealType.label: String
    get() = when (this) {
        MealType.LUNCH -> "午餐"
        MealType.DINNER -> "晚餐"
    }
