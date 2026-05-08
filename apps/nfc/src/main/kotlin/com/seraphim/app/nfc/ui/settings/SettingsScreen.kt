package com.seraphim.app.nfc.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "设置",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // 安全设置
            Text(
                text = "安全",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = { Text("应用锁") },
                supportingContent = { Text("使用指纹或面容解锁") },
                leadingContent = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingContent = {
                    Switch(
                        checked = uiState.appLockEnabled,
                        onCheckedChange = { viewModel.toggleAppLock(it) },
                    )
                },
            )

            ListItem(
                headlineContent = { Text("生物识别") },
                supportingContent = { Text("优先使用指纹/面容") },
                leadingContent = {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                },
                trailingContent = {
                    Switch(
                        checked = uiState.biometricEnabled,
                        onCheckedChange = { viewModel.toggleBiometric(it) },
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // NFC 设置
            Text(
                text = "NFC",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = { Text("自动读取") },
                supportingContent = { Text("检测到标签时自动读取") },
                leadingContent = {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                },
                trailingContent = {
                    Switch(
                        checked = uiState.autoReadEnabled,
                        onCheckedChange = { viewModel.toggleAutoRead(it) },
                    )
                },
            )

            ListItem(
                headlineContent = { Text("震动反馈") },
                supportingContent = { Text("读卡/模拟成功时震动") },
                leadingContent = {
                    Icon(Icons.Default.Vibration, contentDescription = null)
                },
                trailingContent = {
                    Switch(
                        checked = uiState.vibrationEnabled,
                        onCheckedChange = { viewModel.toggleVibration(it) },
                    )
                },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // 数据管理
            Text(
                text = "数据",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = { Text("备份卡片数据") },
                supportingContent = { Text("导出加密备份文件") },
                leadingContent = {
                    Icon(Icons.Default.Save, contentDescription = null)
                },
                modifier = Modifier.clickable { viewModel.backupData() },
            )

            ListItem(
                headlineContent = { Text("恢复卡片数据") },
                supportingContent = { Text("从备份文件恢复") },
                leadingContent = {
                    Icon(Icons.Default.Save, contentDescription = null)
                },
                modifier = Modifier.clickable { viewModel.restoreData() },
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // 关于
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = { Text("钥界 KeyWorld") },
                supportingContent = { Text("版本 1.0.0") },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = null)
                },
            )
        }
    }
}
