package com.seraphim.pokemon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seraphim.pokemon.shared.data.db.entity.PokemonFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonFavoriteDao {

    @Query("SELECT * FROM pokemon_favorite ORDER BY created_at DESC")
    fun getAll(): Flow<List<PokemonFavoriteEntity>>

    @Query("SELECT pokemon_id FROM pokemon_favorite")
    fun getAllIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM pokemon_favorite WHERE pokemon_id = :pokemonId)")
    fun isFavorite(pokemonId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM pokemon_favorite WHERE pokemon_id = :pokemonId)")
    suspend fun isFavoriteOnce(pokemonId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(entity: PokemonFavoriteEntity)

    @Query("DELETE FROM pokemon_favorite WHERE pokemon_id = :pokemonId")
    suspend fun remove(pokemonId: Int)

    @Query("SELECT COUNT(*) FROM pokemon_favorite")
    fun count(): Flow<Int>
}
