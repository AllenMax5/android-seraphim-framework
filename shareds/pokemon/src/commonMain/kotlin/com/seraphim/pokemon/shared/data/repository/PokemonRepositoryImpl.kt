package com.seraphim.pokemon.shared.data.repository

import com.seraphim.core.network.BffResult
import com.seraphim.pokemon.shared.data.db.dao.EvolutionChainDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonDetailDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonFavoriteDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonListDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonSpeciesDao
import com.seraphim.pokemon.shared.data.db.dao.TypeDao
import com.seraphim.pokemon.shared.data.db.entity.PokemonFavoriteEntity
import com.seraphim.pokemon.shared.data.db.mapper.isExpired
import com.seraphim.pokemon.shared.data.db.mapper.toDetailEntity
import com.seraphim.pokemon.shared.data.db.mapper.toEntity
import com.seraphim.pokemon.shared.data.db.mapper.toListEntity
import com.seraphim.pokemon.shared.data.network.PokeApiService
import com.seraphim.pokemon.shared.domain.mapper.toDomainModel
import com.seraphim.pokemon.shared.domain.model.EvolutionChain
import com.seraphim.pokemon.shared.domain.model.PokemonDetail
import com.seraphim.pokemon.shared.domain.model.PokemonListItem
import com.seraphim.pokemon.shared.domain.model.PokemonSpecies
import com.seraphim.pokemon.shared.domain.model.TypeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.Clock

/**
 * 离线优先 Repository 实现
 *
 * 数据链路:
 *   UI ← Flow(Room) ← Repository →  API
 *                                      ↓
 *                                    Room (upsert)
 *
 * - 每个 getter 返回 Room 的 Flow（实时推送）
 * - Flow.onStart 中检查缓存是否过期，过期则静默刷新
 * - 刷新失败不影响 UI，继续使用旧缓存
 */
class PokemonRepositoryImpl(
    private val api: PokeApiService,
    private val pokemonListDao: PokemonListDao,
    private val pokemonDetailDao: PokemonDetailDao,
    private val pokemonSpeciesDao: PokemonSpeciesDao,
    private val evolutionChainDao: EvolutionChainDao,
    private val typeDao: TypeDao,
    private val favoriteDao: PokemonFavoriteDao,
) : PokemonRepository {

    // ─── 列表 ────────────────────────────────────────────────────

    override fun getPokemonList(offset: Int, limit: Int): Flow<List<PokemonListItem>> {
        return combine(
            pokemonListDao.getPage(limit, offset),
            favoriteDao.getAllIds(),
        ) { entities, favoriteIds ->
            entities.map { entity ->
                entity.toDomainModel().copy(isFavorite = entity.id in favoriteIds)
            }
        }.onStart {
            // 首次或过期时自动刷新
            val count = pokemonListDao.count()
            if (count <= offset) {
                refreshPokemonList(offset, limit)
            }
        }
    }

    override suspend fun refreshPokemonList(offset: Int, limit: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        when (val result = api.getPokemonList(limit, offset)) {
            is BffResult.Success -> {
                val resourceList = result.response
                // 对每个资源获取详细数据以拿到 sprite 和 types
                val entities = resourceList.results.mapNotNull { resource ->
                    when (val detail = api.getPokemon(resource.id)) {
                        is BffResult.Success -> {
                            // 同时写入详情缓存
                            pokemonDetailDao.upsert(detail.response.toDetailEntity(now))
                            detail.response.toListEntity(now)
                        }

                        is BffResult.Failure -> null
                    }
                }
                if (entities.isNotEmpty()) {
                    pokemonListDao.upsertAll(entities)
                }
                return true
            }

            is BffResult.Failure -> return false
        }
    }

    override fun searchPokemon(query: String): Flow<List<PokemonListItem>> {
        return combine(
            pokemonListDao.searchByName("%$query%"),
            favoriteDao.getAllIds(),
        ) { entities, favoriteIds ->
            entities.map { entity ->
                entity.toDomainModel().copy(isFavorite = entity.id in favoriteIds)
            }
        }
    }

    // ─── 详情 ────────────────────────────────────────────────────

    override fun getPokemonDetail(id: Int): Flow<PokemonDetail?> {
        return combine(
            pokemonDetailDao.getById(id),
            favoriteDao.isFavorite(id),
        ) { entity, isFav ->
            entity?.toDomainModel()?.copy(isFavorite = isFav)
        }.onStart {
            val cached = pokemonDetailDao.getByIdOnce(id)
            val now = Clock.System.now().toEpochMilliseconds()
            if (cached == null || isExpired(cached.lastUpdatedAt, now)) {
                refreshPokemonDetail(id)
            }
        }
    }

    override suspend fun refreshPokemonDetail(id: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return when (val result = api.getPokemon(id)) {
            is BffResult.Success -> {
                pokemonDetailDao.upsert(result.response.toDetailEntity(now))
                // 同步更新列表缓存
                pokemonListDao.upsertAll(listOf(result.response.toListEntity(now)))
                true
            }

            is BffResult.Failure -> false
        }
    }

    // ─── 物种 ────────────────────────────────────────────────────

    override fun getPokemonSpecies(id: Int): Flow<PokemonSpecies?> {
        return pokemonSpeciesDao.getById(id).map { entity ->
            entity?.toDomainModel()
        }.onStart {
            val cached = pokemonSpeciesDao.getByIdOnce(id)
            val now = Clock.System.now().toEpochMilliseconds()
            if (cached == null || isExpired(cached.lastUpdatedAt, now)) {
                refreshPokemonSpecies(id)
            }
        }
    }

    override suspend fun refreshPokemonSpecies(id: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return when (val result = api.getPokemonSpecies(id)) {
            is BffResult.Success -> {
                pokemonSpeciesDao.upsert(result.response.toEntity(now))
                true
            }

            is BffResult.Failure -> false
        }
    }

    // ─── 进化链 ──────────────────────────────────────────────────

    override fun getEvolutionChain(id: Int): Flow<EvolutionChain?> {
        return evolutionChainDao.getById(id).map { entity ->
            entity?.toDomainModel()
        }.onStart {
            val cached = evolutionChainDao.getByIdOnce(id)
            val now = Clock.System.now().toEpochMilliseconds()
            if (cached == null || isExpired(cached.lastUpdatedAt, now)) {
                refreshEvolutionChain(id)
            }
        }
    }

    override suspend fun refreshEvolutionChain(id: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return when (val result = api.getEvolutionChain(id)) {
            is BffResult.Success -> {
                evolutionChainDao.upsert(result.response.toEntity(now))
                true
            }

            is BffResult.Failure -> false
        }
    }

    // ─── 属性 ────────────────────────────────────────────────────

    override fun getTypeDetail(id: Int): Flow<TypeDetail?> {
        return typeDao.getById(id).map { entity ->
            entity?.toDomainModel()
        }.onStart {
            val cached = typeDao.getByIdOnce(id)
            val now = Clock.System.now().toEpochMilliseconds()
            if (cached == null || isExpired(cached.lastUpdatedAt, now)) {
                refreshTypeDetail(id)
            }
        }
    }

    override suspend fun refreshTypeDetail(id: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return when (val result = api.getType(id)) {
            is BffResult.Success -> {
                typeDao.upsertAll(listOf(result.response.toEntity(now)))
                true
            }

            is BffResult.Failure -> false
        }
    }

    override fun getAllTypes(): Flow<List<TypeDetail>> {
        return typeDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }.onStart {
            val count = typeDao.count()
            if (count == 0) {
                refreshAllTypes()
            }
        }
    }

    override suspend fun refreshAllTypes(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        return when (val result = api.getTypeList()) {
            is BffResult.Success -> {
                val entities = result.response.results.mapNotNull { resource ->
                    when (val typeResult = api.getType(resource.id)) {
                        is BffResult.Success -> typeResult.response.toEntity(now)
                        is BffResult.Failure -> null
                    }
                }
                if (entities.isNotEmpty()) {
                    typeDao.upsertAll(entities)
                }
                true
            }

            is BffResult.Failure -> false
        }
    }

    // ─── 收藏 ────────────────────────────────────────────────────

    override fun getFavoriteIds(): Flow<List<Int>> = favoriteDao.getAllIds()

    override suspend fun toggleFavorite(pokemonId: Int) {
        val exists = favoriteDao.isFavoriteOnce(pokemonId)
        if (exists) {
            favoriteDao.remove(pokemonId)
        } else {
            favoriteDao.add(
                PokemonFavoriteEntity(
                    pokemonId = pokemonId,
                    createdAt = Clock.System.now().toEpochMilliseconds(),
                ),
            )
        }
    }

    override fun isFavorite(pokemonId: Int): Flow<Boolean> = favoriteDao.isFavorite(pokemonId)
}
