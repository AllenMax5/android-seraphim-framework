package com.seraphim.app.yxsg.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seraphim.app.yxsg.ui.components.DonutChart
import com.seraphim.app.yxsg.ui.components.MealCheckInButton
import com.seraphim.app.yxsg.ui.theme.LimitReachedColor
import com.seraphim.app.yxsg.ui.theme.PageHorizontalPadding
import com.seraphim.app.yxsg.ui.theme.PageVerticalPadding
import com.seraphim.app.yxsg.ui.theme.Spacing
import com.seraphim.delicacies.shared.domain.model.MealType
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckInScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: CheckInViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats by viewModel.monthlyStats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PageHorizontalPadding, vertical = PageVerticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Welcome banner - 使用 Surface + tonalElevation 区分层级
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Text(
                text = uiState.welcomeMessage,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(
                    horizontal = Spacing.extraLarge,
                    vertical = Spacing.large
                ),
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Check-in buttons section
        Text(
            text = "今日签到",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(Spacing.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.large),
        ) {
            MealCheckInButton(
                mealType = MealType.LUNCH,
                checkedIn = uiState.lunchCheckedIn,
                enabled = !stats.isLimitReached && !uiState.isLoading,
                onClick = { viewModel.checkIn(MealType.LUNCH) },
                modifier = Modifier.weight(1f),
            )
            MealCheckInButton(
                mealType = MealType.DINNER,
                checkedIn = uiState.dinnerCheckedIn,
                enabled = !stats.isLimitReached && !uiState.isLoading,
                onClick = { viewModel.checkIn(MealType.DINNER) },
                modifier = Modifier.weight(1f),
            )
        }

        if (stats.isLimitReached) {
            Spacer(modifier = Modifier.height(Spacing.medium))
            AssistChip(
                onClick = { /* 无操作，仅展示 */ },
                label = { Text("本月签到已达上限 ✨") },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = LimitReachedColor,
                ),
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Monthly stats chart - 使用 ElevatedCard 提升层级
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.extraLarge, vertical = Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "本月用餐统计",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(Spacing.large))
                DonutChart(stats = stats)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}
