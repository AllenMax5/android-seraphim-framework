package com.seraphim.pokemon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seraphim.pokemon.shared.data.db.entity.EvolutionChainEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvolutionChainDao {

    @Query("SELECT * FROM evolution_chain WHERE id = :id")
    fun getById(id: Int): Flow<EvolutionChainEntity?>

    @Query("SELECT * FROM evolution_chain WHERE id = :id")
    suspend fun getByIdOnce(id: Int): EvolutionChainEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EvolutionChainEntity)

    @Query("DELETE FROM evolution_chain")
    suspend fun deleteAll()
}
