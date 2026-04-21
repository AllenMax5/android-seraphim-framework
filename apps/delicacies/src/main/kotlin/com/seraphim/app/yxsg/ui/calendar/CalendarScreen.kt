package com.seraphim.app.yxsg.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.components.DayStatusIndicator
import com.seraphim.app.yxsg.ui.theme.PageHorizontalPadding
import com.seraphim.app.yxsg.ui.theme.PageVerticalPadding
import com.seraphim.app.yxsg.ui.theme.Spacing
import com.seraphim.delicacies.shared.domain.model.DayCheckInStatus
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock

@Composable
fun CalendarScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val dayStatusMap by viewModel.dayStatusMap.collectAsState()
    val stats by viewModel.monthlyStats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PageHorizontalPadding, vertical = PageVerticalPadding),
    ) {
        // 日历主区域 - 统一使用 Surface 替代多层 Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount > 50f) {
                            viewModel.previousMonth()
                        } else if (dragAmount < -50f) {
                            viewModel.nextMonth()
                        }
                    }
                },
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier.padding(vertical = Spacing.medium),
            ) {
                // Month header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = "上个月",
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.monthLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${stats.totalCount} / ${stats.maxCount} 次",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "下个月",
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = Spacing.large,
                        vertical = Spacing.small
                    ),
                )

                // Weekday headers
                val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                ) {
                    weekdays.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.small))

                // Calendar grid
                CalendarGrid(
                    year = uiState.year,
                    month = uiState.month,
                    dayStatusMap = dayStatusMap,
                    selectedDate = uiState.selectedDate,
                    onDateClick = { viewModel.selectDate(it) },
                    modifier = Modifier.padding(horizontal = Spacing.medium),
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.large))

        // Legend
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.large),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LegendItem(indicator = { DayStatusIndicator(count = 0) }, label = "未签到")
                LegendItem(indicator = { DayStatusIndicator(count = 1) }, label = "1次")
                LegendItem(indicator = { DayStatusIndicator(count = 2) }, label = "2次")
            }
        }
    }

    // Day detail dialog
    if (uiState.showDayDetail && uiState.selectedDate != null) {
        DayDetailDialog(
            date = uiState.selectedDate!!,
            dayStatus = dayStatusMap[uiState.selectedDate],
            onDismiss = { viewModel.dismissDayDetail() },
            onToggle = { mealType, checked ->
                viewModel.updateCheckIn(uiState.selectedDate!!, mealType, checked)
            },
        )
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    dayStatusMap: Map<LocalDate, DayCheckInStatus>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstDay = LocalDate(year, month, 1)
    val daysInMonth = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }

    val startDayOfWeek = when (firstDay.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }

    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val totalCells = startDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = modifier) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startDayOfWeek + 1

                    if (day in 1..daysInMonth) {
                        val date = LocalDate(year, month, day)
                        val status = dayStatusMap[date]
                        val isToday = date == today
                        val isSelected = date == selectedDate

                        DayCell(
                            day = day,
                            status = status,
                            isToday = isToday,
                            isSelected = isSelected,
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Box(modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    status: DayCheckInStatus?,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val count = status?.checkInCount ?: 0
    val dateDesc = "${day}日${if (count > 0) "，已签到$count 次" else ""}"

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }

    val borderModifier = when {
        isToday -> Modifier.border(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = CircleShape,
        )

        else -> Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(borderModifier)
            .background(backgroundColor, CircleShape)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = dateDesc
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
                    isToday -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )
            DayStatusIndicator(count = count)
        }
    }
}

@Composable
private fun LegendItem(
    indicator: @Composable () -> Unit,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        indicator()
        Spacer(modifier = Modifier.size(Spacing.small))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
