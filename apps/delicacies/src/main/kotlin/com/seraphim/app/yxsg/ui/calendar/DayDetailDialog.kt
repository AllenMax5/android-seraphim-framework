package com.seraphim.app.yxsg.ui.calendar

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import com.seraphim.delicacies.shared.domain.model.MealType
import kotlinx.datetime.LocalDate

@Composable
fun DayDetailDialog(
    date: LocalDate,
    dayStatus: DayCheckInStatus?,
    onDismiss: () -> Unit,
    onToggle: (MealType, Boolean) -> Unit,
) {
    var lunchChecked by remember(dayStatus) {
        mutableStateOf(dayStatus?.lunchCheckedIn ?: false)
    }
    var dinnerChecked by remember(dayStatus) {
        mutableStateOf(dayStatus?.dinnerCheckedIn ?: false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${date.year} 年 ${date.monthNumber} 月 ${date.dayOfMonth} 日",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                // Lunch row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.LunchDining,
                        contentDescription = null,
                        tint = LunchColor,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "午餐",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Checkbox(
                        checked = lunchChecked,
                        onCheckedChange = { lunchChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = LunchColor),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Dinner row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.DinnerDining,
                        contentDescription = null,
                        tint = DinnerColor,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "晚餐",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Checkbox(
                        checked = dinnerChecked,
                        onCheckedChange = { dinnerChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = DinnerColor),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val origLunch = dayStatus?.lunchCheckedIn ?: false
                    val origDinner = dayStatus?.dinnerCheckedIn ?: false
                    if (lunchChecked != origLunch) {
                        onToggle(MealType.LUNCH, lunchChecked)
                    }
                    if (dinnerChecked != origDinner) {
                        onToggle(MealType.DINNER, dinnerChecked)
                    }
                    onDismiss()
                },
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )
}
