package com.seraphim.app.nfc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.seraphim.app.nfc.navigation.BottomNavItem
import com.seraphim.app.nfc.navigation.KeyWorldBottomNav
import com.seraphim.app.nfc.nfc.NfcManager
import com.seraphim.app.nfc.ui.emulate.EmulateScreen
import com.seraphim.app.nfc.ui.home.HomeScreen
import com.seraphim.app.nfc.ui.settings.SettingsScreen
import com.seraphim.app.nfc.ui.theme.KeyWorldTheme
import com.seraphim.app.nfc.ui.wallet.ReadResult
import com.seraphim.app.nfc.ui.wallet.WalletScreen
import com.seraphim.app.nfc.ui.wallet.WalletViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * MainActivity - NFC 入口 Activity
 *
 * 遵循官方 NFC 基础文档设计：
 * - 处理三种 NFC Intent：NDEF_DISCOVERED / TECH_DISCOVERED / TAG_DISCOVERED
 * - 使用 IntentCompat 获取 Parcelable（兼容 Android 13+）
 * - singleTask launchMode 确保 onNewIntent 正确接收
 */
class MainActivity : ComponentActivity() {

    private val walletViewModel: WalletViewModel by inject()
    private lateinit var nfcManager: NfcManager
    private lateinit var snackbarHostState: SnackbarHostState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        snackbarHostState = SnackbarHostState()

        // 初始化 NfcManager（使用 ReaderMode，绕过 BAL 限制）
        nfcManager = NfcManager(
            activity = this,
            onTagDiscovered = { cardInfo ->
                walletViewModel.saveCard(cardInfo)
            },
            onError = { message ->
                lifecycleScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            },
        )

        setContent {
            KeyWorldTheme {
                KeyWorldApp(
                    snackbarHostState = snackbarHostState,
                    walletViewModel = walletViewModel,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcManager.enableReaderMode()
    }

    override fun onPause() {
        super.onPause()
        nfcManager.disableReaderMode()
    }
}

@Composable
fun KeyWorldApp(
    snackbarHostState: SnackbarHostState,
    walletViewModel: WalletViewModel,
) {
    val navController = rememberNavController()
    val lastReadResult by walletViewModel.lastReadResult.collectAsState()

    // 监听读卡结果，显示 Snackbar
    LaunchedEffect(lastReadResult) {
        when (val result = lastReadResult) {
            is ReadResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                walletViewModel.clearReadResult()
            }
            is ReadResult.Error -> {
                snackbarHostState.showSnackbar(result.message)
                walletViewModel.clearReadResult()
            }
            null -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            KeyWorldBottomNav(navController = navController)
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(snackbarHostState = snackbarHostState)
            }
            composable(BottomNavItem.Wallet.route) {
                WalletScreen(
                    snackbarHostState = snackbarHostState,
                    viewModel = walletViewModel,
                )
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(snackbarHostState = snackbarHostState)
            }
            composable("emulate/{cardId}") { backStackEntry ->
                val cardId = backStackEntry.arguments?.getString("cardId")
                // TODO: 从 ViewModel 获取卡片数据
                EmulateScreen(
                    card = null,
                    isActive = false,
                    statusLog = emptyList(),
                    onActivate = {},
                    onDeactivate = {},
                    onSelectCard = {},
                )
            }
        }
    }
}
