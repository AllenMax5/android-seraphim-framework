package com.seraphim.pokemon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seraphim.pokemon.shared.data.db.entity.PokemonListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonListDao {

    @Query("SELECT * FROM pokemon_list ORDER BY id ASC")
    fun getAll(): Flow<List<PokemonListEntity>>

    @Query("SELECT * FROM pokemon_list ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getPage(limit: Int, offset: Int): Flow<List<PokemonListEntity>>

    @Query("SELECT * FROM pokemon_list WHERE id = :id")
    fun getById(id: Int): Flow<PokemonListEntity?>

    @Query("SELECT * FROM pokemon_list WHERE id = :id")
    suspend fun getByIdOnce(id: Int): PokemonListEntity?

    @Query("SELECT COUNT(*) FROM pokemon_list")
    suspend fun count(): Int

    @Query("SELECT * FROM pokemon_list WHERE name LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchByName(query: String): Flow<List<PokemonListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<PokemonListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PokemonListEntity)

    @Query("DELETE FROM pokemon_list")
    suspend fun deleteAll()
}
