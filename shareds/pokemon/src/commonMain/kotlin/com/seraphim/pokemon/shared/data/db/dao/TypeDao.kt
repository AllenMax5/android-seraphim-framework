package com.seraphim.pokemon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seraphim.pokemon.shared.data.db.entity.TypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TypeDao {

    @Query("SELECT * FROM type ORDER BY id ASC")
    fun getAll(): Flow<List<TypeEntity>>

    @Query("SELECT * FROM type WHERE id = :id")
    fun getById(id: Int): Flow<TypeEntity?>

    @Query("SELECT * FROM type WHERE id = :id")
    suspend fun getByIdOnce(id: Int): TypeEntity?

    @Query("SELECT COUNT(*) FROM type")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<TypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TypeEntity)

    @Query("DELETE FROM type")
    suspend fun deleteAll()
}
