package com.seraphim.pokemon.shared.data.network

import com.seraphim.core.network.BffResult
import com.seraphim.core.network.receiveBffResult
import com.seraphim.pokemon.shared.data.network.dto.EvolutionChainDto
import com.seraphim.pokemon.shared.data.network.dto.NamedApiResourceList
import com.seraphim.pokemon.shared.data.network.dto.PokemonDto
import com.seraphim.pokemon.shared.data.network.dto.PokemonSpeciesDto
import com.seraphim.pokemon.shared.data.network.dto.TypeDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get

/**
 * PokeAPI v2 HTTP 接口封装
 */
class PokeApiService(private val client: HttpClient) {

    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2"
    }

    // ─── Pokemon ────────────────────────────────────────────────

    /** 分页获取宝可梦列表 */
    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): BffResult<NamedApiResourceList> =
        receiveBffResult { client.get("$BASE_URL/pokemon?limit=$limit&offset=$offset") }

    /** 获取单只宝可梦完整数据 */
    suspend fun getPokemon(id: Int): BffResult<PokemonDto> =
        receiveBffResult { client.get("$BASE_URL/pokemon/$id") }

    /** 按名称获取宝可梦 */
    suspend fun getPokemonByName(name: String): BffResult<PokemonDto> =
        receiveBffResult { client.get("$BASE_URL/pokemon/$name") }

    // ─── Species ────────────────────────────────────────────────

    /** 获取宝可梦物种数据（图鉴描述、进化链引用等） */
    suspend fun getPokemonSpecies(id: Int): BffResult<PokemonSpeciesDto> =
        receiveBffResult { client.get("$BASE_URL/pokemon-species/$id") }

    // ─── Evolution ──────────────────────────────────────────────

    /** 获取进化链 */
    suspend fun getEvolutionChain(id: Int): BffResult<EvolutionChainDto> =
        receiveBffResult { client.get("$BASE_URL/evolution-chain/$id") }

    // ─── Type ───────────────────────────────────────────────────

    /** 获取属性详情（克制关系） */
    suspend fun getType(id: Int): BffResult<TypeDto> =
        receiveBffResult { client.get("$BASE_URL/type/$id") }

    /** 获取所有属性列表 */
    suspend fun getTypeList(limit: Int = 30): BffResult<NamedApiResourceList> =
        receiveBffResult { client.get("$BASE_URL/type?limit=$limit") }
}
