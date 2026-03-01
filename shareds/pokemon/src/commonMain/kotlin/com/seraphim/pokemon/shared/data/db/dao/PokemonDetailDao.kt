package com.seraphim.pokemon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seraphim.pokemon.shared.data.db.entity.PokemonDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDetailDao {

    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    fun getById(id: Int): Flow<PokemonDetailEntity?>

    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    suspend fun getByIdOnce(id: Int): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PokemonDetailEntity)

    @Query("DELETE FROM pokemon_detail WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM pokemon_detail")
    suspend fun deleteAll()
}
