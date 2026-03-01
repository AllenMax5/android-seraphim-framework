package com.seraphim.pokemon.shared.data.repository

import com.seraphim.pokemon.shared.domain.model.EvolutionChain
import com.seraphim.pokemon.shared.domain.model.PokemonDetail
import com.seraphim.pokemon.shared.domain.model.PokemonListItem
import com.seraphim.pokemon.shared.domain.model.PokemonSpecies
import com.seraphim.pokemon.shared.domain.model.TypeDetail
import kotlinx.coroutines.flow.Flow

/**
 * 宝可梦数据仓库 — 所有数据操作的单一入口
 *
 * 策略: API → Room → UI（离线优先 / 缓存优先）
 *  - Room 是 UI 的唯一数据来源（Single Source of Truth）
 *  - API 获取到的数据先写入 Room，再由 Room Flow 推送 UI
 *  - 网络失败时 UI 依然能展示上次缓存的数据
 */
interface PokemonRepository {

    // ─── 列表 ────────────────────────────────────────────────────

    /** 获取宝可梦列表（按页），返回 Flow，Room 变更时自动推送 */
    fun getPokemonList(offset: Int, limit: Int): Flow<List<PokemonListItem>>

    /** 从 API 刷新列表并写入 Room，成功返回 true */
    suspend fun refreshPokemonList(offset: Int, limit: Int): Boolean

    /** 搜索宝可梦 */
    fun searchPokemon(query: String): Flow<List<PokemonListItem>>

    // ─── 详情 ────────────────────────────────────────────────────

    /** 获取宝可梦详情 Flow */
    fun getPokemonDetail(id: Int): Flow<PokemonDetail?>

    /** 从 API 刷新单只宝可梦详情 */
    suspend fun refreshPokemonDetail(id: Int): Boolean

    // ─── 物种 ────────────────────────────────────────────────────

    /** 获取物种信息 Flow */
    fun getPokemonSpecies(id: Int): Flow<PokemonSpecies?>

    /** 从 API 刷新物种信息 */
    suspend fun refreshPokemonSpecies(id: Int): Boolean

    // ─── 进化链 ──────────────────────────────────────────────────

    /** 获取进化链 Flow */
    fun getEvolutionChain(id: Int): Flow<EvolutionChain?>

    /** 从 API 刷新进化链 */
    suspend fun refreshEvolutionChain(id: Int): Boolean

    // ─── 属性 ────────────────────────────────────────────────────

    /** 获取属性详情 Flow */
    fun getTypeDetail(id: Int): Flow<TypeDetail?>

    /** 从 API 刷新属性数据 */
    suspend fun refreshTypeDetail(id: Int): Boolean

    /** 获取全部属性列表 */
    fun getAllTypes(): Flow<List<TypeDetail>>

    /** 从 API 刷新全部属性 */
    suspend fun refreshAllTypes(): Boolean

    // ─── 收藏 ────────────────────────────────────────────────────

    /** 获取所有收藏的宝可梦 ID */
    fun getFavoriteIds(): Flow<List<Int>>

    /** 切换收藏状态 */
    suspend fun toggleFavorite(pokemonId: Int)

    /** 是否已收藏 */
    fun isFavorite(pokemonId: Int): Flow<Boolean>
}
