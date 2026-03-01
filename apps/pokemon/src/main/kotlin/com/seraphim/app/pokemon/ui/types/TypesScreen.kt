package com.seraphim.app.pokemon.ui.types

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seraphim.pokemon.shared.domain.model.TypeDetail
import org.koin.androidx.compose.koinViewModel

@Composable
fun TypesScreen(
    viewModel: TypesViewModel = koinViewModel(),
) {
    val types by viewModel.types.collectAsState()

    if (types.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(types, key = { it.id }) { type ->
                TypeCard(type)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TypeCard(type: TypeDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = type.localizedName ?: type.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (type.damageRelations.doubleDamageTo.isNotEmpty()) {
                Text("效果拔群 →", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    type.damageRelations.doubleDamageTo.forEach { ref ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(ref.name.replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
            }

            if (type.damageRelations.halfDamageTo.isNotEmpty()) {
                Text("效果不佳 →", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    type.damageRelations.halfDamageTo.forEach { ref ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(ref.name.replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
            }

            if (type.damageRelations.doubleDamageFrom.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("弱点 ←", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    type.damageRelations.doubleDamageFrom.forEach { ref ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(ref.name.replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
            }
        }
    }
}
