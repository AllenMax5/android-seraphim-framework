package com.seraphim.nfc.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seraphim.nfc.shared.data.db.entity.HillCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HillCardDao {

    @Query("SELECT * FROM hill_cards ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<HillCardEntity>>

    @Query("SELECT * FROM hill_cards ORDER BY createdAt DESC")
    suspend fun getAll(): List<HillCardEntity>

    @Query("SELECT * FROM hill_cards WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): HillCardEntity?

    @Query("SELECT * FROM hill_cards WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun observeByGroup(groupId: String): Flow<List<HillCardEntity>>

    @Query("SELECT * FROM hill_cards WHERE uid = :uid LIMIT 1")
    suspend fun getByUid(uid: String): HillCardEntity?

    @Query("SELECT * FROM hill_cards WHERE name LIKE '%' || :query || '%' OR uid LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<HillCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: HillCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<HillCardEntity>)

    @Update
    suspend fun update(card: HillCardEntity)

    @Delete
    suspend fun delete(card: HillCardEntity)

    @Query("DELETE FROM hill_cards WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM hill_cards")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM hill_cards WHERE groupId = :groupId")
    suspend fun countByGroup(groupId: String): Int
}
