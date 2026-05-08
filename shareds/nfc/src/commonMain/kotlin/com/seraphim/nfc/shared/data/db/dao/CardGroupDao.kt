package com.seraphim.nfc.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seraphim.nfc.shared.data.db.entity.CardGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardGroupDao {

    @Query("SELECT * FROM card_groups ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<CardGroupEntity>>

    @Query("SELECT * FROM card_groups ORDER BY sortOrder ASC, name ASC")
    suspend fun getAll(): List<CardGroupEntity>

    @Query("SELECT * FROM card_groups WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): CardGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: CardGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<CardGroupEntity>)

    @Update
    suspend fun update(group: CardGroupEntity)

    @Delete
    suspend fun delete(group: CardGroupEntity)

    @Query("DELETE FROM card_groups WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM card_groups")
    suspend fun count(): Int
}
