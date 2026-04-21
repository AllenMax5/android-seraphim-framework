package com.seraphim.app.yxsg.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.app.yxsg.ui.theme.Spacing
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import com.seraphim.delicacies.shared.domain.model.MealType
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailDialog(
    date: LocalDate,
    dayStatus: DayCheckInStatus?,
    onDismiss: () -> Unit,
    onToggle: (MealType, Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 2.dp,
    ) {
        DayDetailContent(
            date = date,
            dayStatus = dayStatus,
            onSave = { lunch, dinner ->
                val origLunch = dayStatus?.lunchCheckedIn ?: false
                val origDinner = dayStatus?.dinnerCheckedIn ?: false
                if (lunch != origLunch) onToggle(MealType.LUNCH, lunch)
                if (dinner != origDinner) onToggle(MealType.DINNER, dinner)

                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onDismiss()
                }
            },
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onDismiss()
                }
            },
        )
    }
}

@Composable
private fun DayDetailContent(
    date: LocalDate,
    dayStatus: DayCheckInStatus?,
    onSave: (lunch: Boolean, dinner: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var lunch by remember(dayStatus) { mutableStateOf(dayStatus?.lunchCheckedIn ?: false) }
    var dinner by remember(dayStatus) { mutableStateOf(dayStatus?.dinnerCheckedIn ?: false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.extraLarge, vertical = Spacing.large),
    ) {
        // Date title
        Text(
            text = "${date.year}年${date.monthNumber}月${date.dayOfMonth}日",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = date.dayOfWeek.displayName(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(0.8f),
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Meal toggles
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                MealToggleItem(
                    mealType = MealType.LUNCH,
                    checked = lunch,
                    onCheckedChange = { lunch = it },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.large))
                MealToggleItem(
                    mealType = MealType.DINNER,
                    checked = dinner,
                    onCheckedChange = { dinner = it },
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Button(
            onClick = { onSave(lunch, dinner) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Text("保存修改")
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun MealToggleItem(
    mealType: MealType,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val mealLabel = when (mealType) {
        MealType.LUNCH -> "午餐"
        MealType.DINNER -> "晚餐"
    }
    val mealIcon = when (mealType) {
        MealType.LUNCH -> Icons.Rounded.LunchDining
        MealType.DINNER -> Icons.Rounded.DinnerDining
    }
    val mealColor = when (mealType) {
        MealType.LUNCH -> LunchColor
        MealType.DINNER -> DinnerColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.large, vertical = Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = mealIcon,
            contentDescription = mealLabel,
            tint = mealColor,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(Spacing.large))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mealLabel,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = if (checked) "已签到" else "未签到",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

/** 获取星期中文名称 */
private fun kotlinx.datetime.DayOfWeek.displayName(): String = when (this) {
    kotlinx.datetime.DayOfWeek.MONDAY -> "星期一"
    kotlinx.datetime.DayOfWeek.TUESDAY -> "星期二"
    kotlinx.datetime.DayOfWeek.WEDNESDAY -> "星期三"
    kotlinx.datetime.DayOfWeek.THURSDAY -> "星期四"
    kotlinx.datetime.DayOfWeek.FRIDAY -> "星期五"
    kotlinx.datetime.DayOfWeek.SATURDAY -> "星期六"
    kotlinx.datetime.DayOfWeek.SUNDAY -> "星期日"
    else -> ""
}
