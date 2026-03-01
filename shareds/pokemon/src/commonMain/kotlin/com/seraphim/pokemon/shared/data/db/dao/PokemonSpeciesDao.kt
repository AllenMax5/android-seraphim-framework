package com.seraphim.pokemon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seraphim.pokemon.shared.data.db.entity.PokemonSpeciesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonSpeciesDao {

    @Query("SELECT * FROM pokemon_species WHERE id = :id")
    fun getById(id: Int): Flow<PokemonSpeciesEntity?>

    @Query("SELECT * FROM pokemon_species WHERE id = :id")
    suspend fun getByIdOnce(id: Int): PokemonSpeciesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PokemonSpeciesEntity)

    @Query("DELETE FROM pokemon_species")
    suspend fun deleteAll()
}
