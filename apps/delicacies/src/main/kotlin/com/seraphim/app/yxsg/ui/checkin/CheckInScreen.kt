package com.seraphim.app.yxsg.ui.checkin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DinnerDining
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import com.seraphim.app.yxsg.ui.theme.DinnerColor
import com.seraphim.app.yxsg.ui.theme.LunchColor
import com.seraphim.app.yxsg.ui.theme.NotCheckedInColor
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Welcome banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                text = uiState.welcomeMessage,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Check-in buttons
        Text(
            text = "今日签到",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MealCheckInCard(
                mealType = MealType.LUNCH,
                label = "午餐",
                icon = { Icon(Icons.Rounded.LunchDining, contentDescription = null, modifier = Modifier.size(32.dp)) },
                checkedIn = uiState.lunchCheckedIn,
                enabled = !uiState.lunchCheckedIn && !stats.isLimitReached && !uiState.isLoading,
                onClick = { viewModel.checkIn(MealType.LUNCH) },
                modifier = Modifier.weight(1f),
            )
            MealCheckInCard(
                mealType = MealType.DINNER,
                label = "晚餐",
                icon = { Icon(Icons.Rounded.DinnerDining, contentDescription = null, modifier = Modifier.size(32.dp)) },
                checkedIn = uiState.dinnerCheckedIn,
                enabled = !uiState.dinnerCheckedIn && !stats.isLimitReached && !uiState.isLoading,
                onClick = { viewModel.checkIn(MealType.DINNER) },
                modifier = Modifier.weight(1f),
            )
        }

        if (stats.isLimitReached) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "本月签到已达上限 🎉",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Monthly stats chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "本月用餐统计",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))
                DonutChart(stats = stats)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MealCheckInCard(
    mealType: MealType,
    label: String,
    icon: @Composable () -> Unit,
    checkedIn: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseColor = if (mealType == MealType.LUNCH) LunchColor else DinnerColor
    val containerColor by animateColorAsState(
        targetValue = if (checkedIn) baseColor.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "containerColor",
    )
    val contentColor by animateColorAsState(
        targetValue = if (checkedIn) baseColor else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300),
        label = "contentColor",
    )

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = if (checkedIn) baseColor.copy(alpha = 0.15f) else NotCheckedInColor.copy(alpha = 0.3f),
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (checkedIn) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = baseColor,
                    modifier = Modifier.size(32.dp),
                )
            } else {
                icon()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                )
                if (checkedIn) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.titleMedium,
                        color = baseColor,
                    )
                }
            }
            Text(
                text = if (checkedIn) "已签到" else "点击签到",
                style = MaterialTheme.typography.bodySmall,
                color = if (checkedIn) baseColor else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
