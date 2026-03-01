package com.seraphim.app.pokemon.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.seraphim.pokemon.shared.domain.model.PokemonDetail
import com.seraphim.pokemon.shared.domain.model.PokemonSpecies
import com.seraphim.pokemon.shared.domain.model.StatValue
import com.seraphim.pokemon.shared.domain.model.totalBaseStat
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBack: () -> Unit,
    viewModel: PokemonDetailViewModel = koinViewModel { parametersOf(pokemonId) },
) {
    val detail by viewModel.detail.collectAsState()
    val species by viewModel.species.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        detail?.name?.replaceFirstChar { it.uppercase() } ?: "加载中...",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "收藏",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        if (detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                detail?.let { pokemon ->
                    DetailHeader(pokemon)
                    Spacer(modifier = Modifier.height(16.dp))
                    species?.let { sp -> SpeciesInfo(sp) }
                    Spacer(modifier = Modifier.height(16.dp))
                    BaseStatsSection(pokemon)
                }
            }
        }
    }
}

@Composable
private fun DetailHeader(pokemon: PokemonDetail) {
    // Sprite
    AsyncImage(
        model = pokemon.sprites.frontDefault,
        contentDescription = pokemon.name,
        modifier = Modifier.size(160.dp),
        contentScale = ContentScale.Fit,
    )

    Text(
        text = "#${pokemon.id.toString().padStart(4, '0')}",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Text(
        text = pokemon.name.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Types
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        pokemon.types.sortedBy { it.slot }.forEach { type ->
            Text(
                text = type.typeName.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Height / Weight
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("身高", style = MaterialTheme.typography.labelMedium)
            Text("${pokemon.height} m", style = MaterialTheme.typography.bodyLarge)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("体重", style = MaterialTheme.typography.labelMedium)
            Text("${pokemon.weight} kg", style = MaterialTheme.typography.bodyLarge)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("基础经验", style = MaterialTheme.typography.labelMedium)
            Text("${pokemon.baseExperience ?: "-"}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun SpeciesInfo(species: PokemonSpecies) {
    Column(modifier = Modifier.fillMaxWidth()) {
        species.genus?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        species.flavorText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun BaseStatsSection(pokemon: PokemonDetail) {
    val maxStat = 255f

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "种族值",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        pokemon.stats.forEach { stat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = statDisplayName(stat.name),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(56.dp),
                )
                Text(
                    text = stat.baseStat.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp),
                )
                LinearProgressIndicator(
                    progress = { (stat.baseStat / maxStat).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "总计: ${pokemon.totalBaseStat}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun statDisplayName(name: String): String = when (name) {
    StatValue.HP -> "HP"
    StatValue.ATTACK -> "攻击"
    StatValue.DEFENSE -> "防御"
    StatValue.SPECIAL_ATTACK -> "特攻"
    StatValue.SPECIAL_DEFENSE -> "特防"
    StatValue.SPEED -> "速度"
    else -> name
}
