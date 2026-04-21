package com.seraphim.app.yxsg.ui.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.seraphim.app.yxsg.ui.theme.PageHorizontalPadding
import com.seraphim.app.yxsg.ui.theme.PageVerticalPadding
import com.seraphim.app.yxsg.ui.theme.Spacing
import com.seraphim.app.yxsg.worker.WorkManagerScheduler
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ScheduleNotifications -> {
                    WorkManagerScheduler.schedule(context)
                }
                is SettingsEvent.CancelNotifications -> {
                    WorkManagerScheduler.cancel(context)
                }
                is SettingsEvent.ExportCsv -> {
                    exportCsvFile(context, event.content)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = PageVerticalPadding),
    ) {
        // Page title
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = PageHorizontalPadding,
                vertical = Spacing.large
            ),
        )

        // Notification group
        SettingsGroup(title = "提醒") {
            ListItem(
                headlineContent = { Text("每日签到提醒") },
                supportingContent = { Text("午餐 11:30 · 晚餐 17:30") },
                leadingContent = {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "提醒",
                    )
                },
                trailingContent = {
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) },
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            )
        }

        Spacer(modifier = Modifier.height(Spacing.large))

        // Data management group
        SettingsGroup(title = "数据管理") {
            ListItem(
                headlineContent = { Text("导出签到数据") },
                supportingContent = { Text("导出为 CSV 文件") },
                leadingContent = {
                    Icon(
                        Icons.Rounded.FileDownload,
                        contentDescription = "导出",
                    )
                },
                modifier = Modifier.clickable { viewModel.exportData() },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = PageHorizontalPadding))

            ListItem(
                headlineContent = {
                    Text(
                        text = "清除所有数据",
                        color = MaterialTheme.colorScheme.error,
                    )
                },
                supportingContent = { Text("删除全部签到记录，不可恢复") },
                leadingContent = {
                    Icon(
                        Icons.Rounded.DeleteForever,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                modifier = Modifier.clickable { showClearConfirmDialog = true },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // App info
        Text(
            text = "云飨食光 v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("确认清除") },
            text = { Text("将删除所有签到记录，此操作不可恢复。确定要继续吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearConfirmDialog = false
                    },
                ) {
                    Text("确认删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("取消")
                }
            },
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = PageHorizontalPadding,
                vertical = Spacing.small
            ),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp,
        ) {
            Column {
                content()
            }
        }
    }
}

private fun exportCsvFile(context: Context, csvContent: String) {
    try {
        val file = File(context.cacheDir, "delicacies_checkin_export.csv")
        file.writeText(csvContent)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "导出签到数据"))
    } catch (_: Exception) {
        // fallback - do nothing
    }
}
