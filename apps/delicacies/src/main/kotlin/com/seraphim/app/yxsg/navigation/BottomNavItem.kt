package com.seraphim.app.yxsg.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    CHECK_IN(
        route = "checkin",
        label = "签到",
        icon = Icons.Rounded.CheckCircle,
    ),
    CALENDAR(
        route = "calendar",
        label = "日历",
        icon = Icons.Rounded.CalendarMonth,
    ),
    SETTINGS(
        route = "settings",
        label = "设置",
        icon = Icons.Rounded.Settings,
    ),
}
